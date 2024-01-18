package com.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.io.CloseMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private final DelayedSemaphore semaphore;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        semaphore = new DelayedSemaphore(timeUnit, requestLimit);
    }

    public void CreateDocument(Document document, String signature) {
        try {
            semaphore.acquire();
        } catch (DelayedSemaphore.LimitExceededException e) {
            return;
        }

        String requestUrl = "https://ismp.crpt.ru/api/v3/lk/documents/create";
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString( document );
            System.out.println(json);

            CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
            client.start();
            SimpleHttpRequest request = SimpleRequestBuilder.post(requestUrl)
                    .setBody(json, ContentType.APPLICATION_JSON)
                    .build();
            client.execute(request, null);
            client.close(CloseMode.IMMEDIATE);
        } catch (Exception ignored) { }
    }

    public static class Document implements Serializable {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private Boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private ArrayList<Product> products;
        private String reg_date;
        private String reg_number;

        public Document(Description description, String docId, String docStatus, String docType, Boolean importRequest, String ownerInn, String participantInn, String producerInn, String productionDate, String productionType, ArrayList<Product> products, String regDate, String regNumber) {
            this.description = description;
            doc_id = docId;
            doc_status = docStatus;
            doc_type = docType;
            this.importRequest = importRequest;
            owner_inn = ownerInn;
            participant_inn = participantInn;
            producer_inn = producerInn;
            production_date = productionDate;
            production_type = productionType;
            this.products = products;
            reg_date = regDate;
            reg_number = regNumber;
        }

        public Description getDescription() {
            return description;
        }

        public String getDoc_id() {
            return doc_id;
        }

        public String getDoc_status() {
            return doc_status;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public Boolean getImportRequest() {
            return importRequest;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public String getParticipant_inn() {
            return participant_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public String getProduction_date() {
            return production_date;
        }

        public String getProduction_type() {
            return production_type;
        }

        public ArrayList<Product> getProducts() {
            return products;
        }

        public String getReg_date() {
            return reg_date;
        }

        public String getReg_number() {
            return reg_number;
        }

        public static class Description implements Serializable {
            private String participantInn;

            public Description(String participantInn) {
                this.participantInn = participantInn;
            }

            public String getParticipantInn() {
                return participantInn;
            }
        }

        public static class Product implements Serializable {
            private String certificate_document;
            private String certificate_document_date;
            private String certificate_document_number;
            private String owner_inn;
            private String producer_inn;
            private String production_date;
            private String tnved_code;
            private String uit_code;
            private String uitu_code;

            public Product(String certificateDocument, String certificateDocumentDate, String certificateDocumentNumber, String ownerInn, String producerInn, String productionDate, String tnvedCode, String uitCode, String uituCode) {
                certificate_document = certificateDocument;
                certificate_document_date = certificateDocumentDate;
                certificate_document_number = certificateDocumentNumber;
                owner_inn = ownerInn;
                producer_inn = producerInn;
                production_date = productionDate;
                tnved_code = tnvedCode;
                uit_code = uitCode;
                uitu_code = uituCode;
            }

            public String getCertificate_document() {
                return certificate_document;
            }

            public String getCertificate_document_date() {
                return certificate_document_date;
            }

            public String getCertificate_document_number() {
                return certificate_document_number;
            }

            public String getOwner_inn() {
                return owner_inn;
            }

            public String getProducer_inn() {
                return producer_inn;
            }

            public String getProduction_date() {
                return production_date;
            }

            public String getTnved_code() {
                return tnved_code;
            }

            public String getUit_code() {
                return uit_code;
            }

            public String getUitu_code() {
                return uitu_code;
            }
        }
    }

    private static class DelayedSemaphore {
        private final Semaphore semaphore;
        private final TimeUnit timeUnit;
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        public DelayedSemaphore(TimeUnit timeUnit, int requestLimit) {
            this.semaphore = new Semaphore(requestLimit);
            this.timeUnit = timeUnit;
        }

        private Long now() {
            return System.currentTimeMillis();
        }

        public void acquire() throws LimitExceededException {
            try {
                semaphore.acquire();
                service.schedule(this::release, 1, timeUnit);
            } catch (InterruptedException ignored) {
                throw new LimitExceededException("Limit exceeded");
            }
        }

        private void release() {
            semaphore.release();
        }

        public static class LimitExceededException extends Exception {
            public LimitExceededException(String errorMessage) {
                super(errorMessage);
            }
        }
    }
}
