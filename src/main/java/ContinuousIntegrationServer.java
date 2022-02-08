import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.eclipse.jgit.api.Git;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

        JSONObject json = new JSONObject();
        String branch = "";
        if (request.getMethod() == "POST") {
            String event = request.getHeader("X-GitHub-Event");
            if (event.compareTo("push") == 0) {
                branch = json.get("ref").toString();
            }
        }
        
        try {
            Git git = Git.cloneRepository()
            .setURI( "https://github.com/asta12/CI-Server.git" )
            .setBranchesToClone(Arrays.asList(branch))
            .setBranch(branch)
            .call();

        System.out.println("repository cloned");
        } catch (Exception e) {
            System.out.println("Couldn't clone repo");
            e.printStackTrace();
        }

        Process process = Runtime.getRuntime().exec("./gradlew test");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        Process process = Runtime.getRuntime().exec("rm -rf CI-Server/");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
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

    public JSONObject getJSON(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
        String s = bufReader.readLine();
        JSONParser parser = new JSONParser();

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj = (JSONObject) parser.parse(s.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }
}