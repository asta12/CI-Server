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

public class CIServerTest {

    /**
        
     */
    @Test
    public void branchTest() throws IOException {
        FileReader fileReader = new FileReader(new File("testFiles/mockRequest.txt"));
        BufferedReader reader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();

        String line = line = reader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            line = reader.readLine();
        }
        reader.close();

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        byte[] payload = stringBuilder.toString().getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(payload);
        when(mockRequest.getInputStream()).thenReturn(new DelegatingServletInputStream(stream));

        ContinuousIntegrationServer CI = new ContinuousIntegrationServer();
        JSONObject json = CI.getJSON(mockRequest);
        assertEquals("refs/heads/testbranch123", json.get("ref").toString());
    }

}

