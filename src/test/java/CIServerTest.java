import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;

public class CIServerTest {

    @Test
    public void branchTest()throws IOException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        byte[] payload = "Object{}".getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(payload);
        when(mockRequest.getInputStream()).thenReturn(new DelegatingServletInputStream(stream));

        ContinuousIntegrationServer CI = new ContinuousIntegrationServer();
         JSONObject json = CI.getJSON(mockRequest);
        //assertEquals("ref/refs/heads/testbranch123", branch);
    }

}