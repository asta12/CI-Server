import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class CIServerTest {

    @Test
    public void branchTest()throws IOException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        byte[] payload = "payload=%7B%22ref%22%3A%22refs%2Fheads%2Ftestbranch123%22%2C%22before%22%3A%22aa0068c8de80c19af57d7372da1b880cf9f396b1%22%2C%22...\n".getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(payload);
        when(mockRequest.getInputStream()).thenReturn(new DelegatingServletInputStream(stream));

        ContinuousIntegrationServer CI = new ContinuousIntegrationServer();
        String branch = CI.getBranch(mockRequest);
        assertEquals("ref/refs/heads/testbranch123/", branch);
    }

}