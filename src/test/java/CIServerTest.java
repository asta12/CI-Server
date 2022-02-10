import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;

import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class CIServerTest {


    /**
     * Tests that the correct branch is retreived from a request
     * @throws IOException throws IOException
     */
    @Test
    public void branchTest() throws IOException {
        FileReader fileReader = new FileReader(new File("testFiles/mockRequest.txt"));
        BufferedReader reader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        byte[] payload = sb.toString().getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(payload);
        when(mockRequest.getInputStream()).thenReturn(new DelegatingServletInputStream(stream));

        ContinuousIntegrationServer CI = new ContinuousIntegrationServer();
        JSONObject json = CI.getJSON(mockRequest);
        assertEquals("refs/heads/testbranch123 dhsjkhfkshfvjk", json.get("ref").toString());
    }

    /**
     * Tests the processCall method to execute the commandline argument "echo"
     * @throws IOException throws IOException
     */
    @Test
    public void processCallTest() throws IOException {
        ContinuousIntegrationServer CI = new ContinuousIntegrationServer();
        String expected = "processCall works";
        String actual = CI.processCall("echo " + expected);
        assertEquals(expected, actual);
    }

}

