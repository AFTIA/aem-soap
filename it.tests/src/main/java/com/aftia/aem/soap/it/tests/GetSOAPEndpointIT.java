package com.aftia.aem.soap.it.tests;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.testing.clients.ClientException;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetSOAPEndpointIT {

    private static CloseableHttpClient httpClient;
    private static String soapEndpointHost = "";


    @BeforeClass
    public static void beforeClass() throws ClientException {
        httpClient = HttpClients.createDefault();
        soapEndpointHost = System.getProperty("sling.it.instance.url.3").toString();
    }

    /**
     * Verifies that the endpoint is bound
     * @throws IOException
     * @throws ClientProtocolException
     */
    @Test
    public void testSoapEndpoint() throws ClientException, ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(soapEndpointHost + "/soap/endpoint?wsdl");
        HttpResponse response = httpClient.execute(httpGet);

        if(response.getStatusLine().getStatusCode() != 200){
            throw new IOException("Invalid response received from SOAP endpoint, check setup");
        }
    }
}
