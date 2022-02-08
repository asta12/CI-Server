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
    /**
     * Handler for the path "/". This is the main handler for the CI-Server.
     */
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
        
        cloneRepo(branch);
        String compileOutput = processCall("gradle javaCompile");
        System.out.println(compileOutput);
        //processCall("./gradlew test");
        processCall("rm -rf CI-Server");
        
    }

    /**
     * @param command String representing a commandline command
     * Runs the command given by the argument
     * @return Returns a String containing what the command outputted.
     */
    public String processCall(String command) throws IOException {
        String line = "";
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    /**
     * @param branch String representing the branch to be cloned.
     * Clones the branch specified by the argument.
     */
    public void cloneRepo(String branch) {
        try {
            Git git = Git.cloneRepository()
            .setURI( "https://github.com/asta12/CI-Server.git" )
            .setBranchesToClone(Arrays.asList(branch))
            .setBranch(branch)
            .call();
            System.out.println("Repository cloned");
        } catch (Exception e) {
            System.out.println("Couldn't clone repository");
            e.printStackTrace();
        }
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }

    /**
     * @param request A HTTP request with a body whose content is in json format.
     * Creates a JSON Object out of the payload of the request.
     * @return A JSONObject that is the payload of the request.
     */
    public JSONObject getJSON(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
        String s = bufReader.readLine();
        JSONParser parser = new JSONParser();

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj = (JSONObject) parser.parse(s.toString());
        } catch (ParseException e) {
            System.out.println("Couldn't parse JSON object");
            e.printStackTrace();
        }

        return jsonObj;
    }
}