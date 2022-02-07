import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        String branch = "";
        if (request.getMethod() == "POST") {
            String event = request.getHeader("X-GitHub-Event");
            if (event.compareTo("push") == 0) {
                branch = getBranch(request);
            }
        }
        

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        //response.getWriter().println("CI job done");
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }

    public String getBranch(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = request.getInputStream();
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
        String s = bufReader.readLine();
        System.out.println(s);
        StringBuilder path = new StringBuilder();
        int index = 14;
        while (true) {
            while (s.charAt(index) != '%') {
                path.append(s.charAt(index));
                index++;
            }
            if (path.toString().compareTo("before") == 0) {
                break;
            } else {
                sb.append(path.toString() + "/");
                path.setLength(0);
                while (s.charAt(index) == '%') {
                    index = index + 3;
                }
            }
        }

        return sb.toString();
    }
}