package edu.gatech.runyourgarden.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.gatech.runyourgarden.model.PMF;
import edu.gatech.runyourgarden.model.Plant;

public class ListPlantServlet extends HttpServlet {
    private PrintWriter writer = null;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        writer = resp.getWriter();
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(Plant.class);
        JSONArray ret = new JSONArray();
        try {
            @SuppressWarnings("unchecked")
            List<Plant> results = (List<Plant>) query.execute();
            if (!results.isEmpty()) {
                for (Plant e : results) {
                    JSONArray jArray = new JSONArray();
                    int cost = e.getCost();
                    String img = e.getImgLocation();
                    int id = e.getPlantId();
                    jArray.put(0, cost);
                    jArray.put(1, img);
                    JSONObject jobj = new JSONObject();
                    jobj.put(""+id, jArray);
                    ret.put(jobj);
                }
                writer.println(ret.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
