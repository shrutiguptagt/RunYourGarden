package edu.gatech.runyourgarden.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import edu.gatech.runyourgarden.model.PMF;
import edu.gatech.runyourgarden.model.Session;
import edu.gatech.runyourgarden.model.UserProfile;

@SuppressWarnings("serial")
public class SessionServlet extends HttpServlet {
    
    private PrintWriter writer = null;
    private String sessionState = null;
    private String userId = null;
    private String gpsInfo = null;
            
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        writer = resp.getWriter();
        sessionState = req.getParameter("session_state");
        userId = req.getParameter("user_id");
        gpsInfo = req.getParameter("gps_update");
        
        if (userId == null || sessionState == null) {
            writer.println("{error:important parameter is missing}");
            return;
        }
        
        if (sessionState.equals("start")) {
            sessionStart();
        } else if (sessionState.equals("stop")) {
            String sessionId = req.getParameter("session_id");
            sessionEnd(sessionId);
        } else if (sessionState.equals("ongoing")) {
            String sessionId = req.getParameter("session_id");
            sessionProces(sessionId);
        }
    }
    
    private void createSession(UserProfile userProfile, String sessionId) {
        Session session = new Session(userProfile, sessionId);
        userProfile.getSessionsSet().add(session);
        System.out.println("Session " + sessionId + " has been created");
    }
    
    private boolean verifySession(String sessionId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(Session.class);
        query.setFilter("sessionId == sessionParam");
        query.declareParameters("String sessionParam");
        try {
            @SuppressWarnings("unchecked")
            List<Session> results = (List<Session>) query.execute(sessionId);
            if (!results.isEmpty()) {
                for (Session e : results) {
                    System.out.println("size " + e.getCoordinates().size());
                    if (e.getCoordinates().size() < 1) { 
                        // need also update UserProfile SessionKeySet
                        Query query1 = pm.newQuery(UserProfile.class);
                        query1.setFilter("userId == uidParam");
                        query1.declareParameters("String uidParam");
                        @SuppressWarnings("unchecked")
                        List<UserProfile> results1 = (List<UserProfile>) query.execute(userId);
                        if (!results1.isEmpty()) {
                            for (UserProfile user : results1) {
                                user.getSessionsSet().remove(e);
                                break;
                            }
                        }
                        pm.deletePersistent(e); 
                        return false;
                    }
                    break;
                }
            }
        } catch (Exception e) {
        } finally {
            pm.close();
        }
        return true;
    };
    
    private void doSomething(){};
    
    private void sessionEnd(String sessionId) {
        JSONObject ret = new JSONObject();
        try {
            if (sessionId == null) {
                ret.put("error", "Session id is missing");
            } else {
                if (verifySession(sessionId)) {
                    ret.put("results", "Session has stopped");                    
                    doSomething(); // Asynchronously?
                } else {
                    ret.put("results", "Session was too short");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        writer.println(ret.toString());
    }
    
    private void sessionStart() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        JSONObject ret = new JSONObject();
        try {
            String sessionId = userId + "_" + System.currentTimeMillis();
            Query query = pm.newQuery(UserProfile.class);
            query.setFilter("userId == lastNameParam");
            query.declareParameters("String lastNameParam");
            @SuppressWarnings("unchecked")
            List<UserProfile> results = (List<UserProfile>) query.execute(userId);
            if (!results.isEmpty()) {
                for (UserProfile e : results) {
                    System.out.println("Start new session for " + e.getUserId());
                    createSession(e, sessionId); // There should be only one
                    pm.close();
                    break;
                }
            }
            ret.put("session_id", sessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        writer.println(ret.toString());
    }
    
    private void sessionProces(String sessionId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        JSONObject ret = new JSONObject();
        try {
            if (sessionId == null) {
                ret.put("error", "Session id is missing");
            } else {
                Query query = pm.newQuery(Session.class);
                query.setFilter("sessionId == sessionParam");
                query.declareParameters("String sessionParam");
                @SuppressWarnings("unchecked")
                List<Session> results = (List<Session>) query.execute(sessionId);
                if (!results.isEmpty()) {
                    for (Session e : results) {
                        e.getCoordinates().add(gpsInfo);
                        System.out.println("Updating new GPS info: " + gpsInfo);
                        pm.close();
                        break;
                    }
                    ret.put("results", "OK");
                } else {
                    System.out.println("Session size " + results.size());
                    ret.put("error", "Session not found");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        writer.println(ret.toString());
    }
}
