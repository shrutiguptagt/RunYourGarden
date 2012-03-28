package edu.gatech.runyourgarden.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.gatech.runyourgarden.model.PMF;
import edu.gatech.runyourgarden.model.UserProfile;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
    private PrintWriter writer = null;
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        writer = resp.getWriter();
        String userid = req.getParameter("user_id");
        Key kwKey = KeyFactory.createKey(
                UserProfile.class.getSimpleName(), userid);
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(UserProfile.class);
        query.setFilter("userId == useridParam");
        query.declareParameters("String useridParam");
        JSONObject ret = new JSONObject();
        try {
            @SuppressWarnings("unchecked")
            List<UserProfile> results = (List<UserProfile>) query.execute(kwKey);
            if (!results.isEmpty()) {
                System.out.println("Found existing user");
                for (UserProfile e : results) {
                    populateProfile(ret, e);
                    break;
                }
            } else {
                System.out.println("Creating a new user");
                UserProfile user = new UserProfile(userid);
                user.setKey(kwKey);
                populateProfile(ret, user);
                pm.makePersistent(user);
            }
        } catch (Exception e) {
            System.out.println("Exception: inserting a new user");
            UserProfile user = new UserProfile(userid);
            user.setKey(kwKey);
            populateProfile(ret, user);
            pm.makePersistent(user);
        } finally {
            pm.close();
        }
        writer.println(ret.toString());
    }
    
    private void populateProfile(JSONObject ret, UserProfile e) {
        try {
            ret.put("acc_calories", e.getAccumCalories());
            ret.put("acc_distance", e.getAccumDistance());
            ret.put("acc_time", e.getAccumTime());
            ret.put("remain_calories", e.getRemainCalories());
            ret.put("remain_distance", e.getRemainDistance());
            ret.put("remain_time", e.getRemainTime());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
