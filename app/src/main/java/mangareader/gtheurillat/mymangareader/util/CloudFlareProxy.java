package mangareader.gtheurillat.mymangareader.util;

import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8RuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloudFlareProxy {

    private String mUrl;
    private String mUser_agent;
    private cfCallback mCallback;
    private int mRetry_count;
    private URL ConnUrl;
    private List<HttpCookie> mCookieList;
    private CookieManager mCookieManager;
    private HttpURLConnection mCheckConn;
    private HttpURLConnection mGetMainConn;
    private HttpURLConnection mGetRedirectionConn;

    private static final int MAX_COUNT = 3;
    private static final int CONN_TIMEOUT = 60000;
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3";

    private boolean canVisit = false;

    public CloudFlareProxy(String url) {
        mUrl = url;
    }

    public CloudFlareProxy(String url, String user_agent) {
        mUrl = url;
        mUser_agent = user_agent;
    }

    public String getUser_agent() {
        return mUser_agent;
    }

    public void setUser_agent(String user_agent) {
        mUser_agent = user_agent;
    }

    public void getCookies(final cfCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                urlThread(callback);
            }
        }).start();
    }

    private void urlThread(cfCallback callback){
        mCookieManager = new CookieManager();
        mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL); //接受所有cookies
        CookieHandler.setDefault(mCookieManager);
        HttpURLConnection.setFollowRedirects(false);

        while (!canVisit){
            if (mRetry_count>MAX_COUNT){
                break;
            }
            try {

                int responseCode = checkUrl();
                if (responseCode==200){
                    canVisit=true;
                    break;
                }else {
                    getVisiteCookie();
                }
            } catch (IOException | InterruptedException e) {
                if (mCookieList!=null){
                    mCookieList= new ArrayList<>(mCookieList);
                    mCookieList.clear();
                }
                e.printStackTrace();
            } finally {
                closeAllConn();
            }
            mRetry_count++;
        }
        if (callback!=null){
            Looper.prepare();
            if (canVisit){
                callback.onSuccess(mCookieList);
            }else {
                e("Get Cookie Failed");
                callback.onFail();
            }


        }
    }



    private void getVisiteCookie() throws IOException, InterruptedException {
        ConnUrl = new URL(mUrl);
        mGetMainConn = (HttpURLConnection) ConnUrl.openConnection();
        mGetMainConn.setRequestMethod("GET");
        mGetMainConn.setConnectTimeout(CONN_TIMEOUT);
        mGetMainConn.setReadTimeout(CONN_TIMEOUT);
        if (!TextUtils.isEmpty(mUser_agent)){
            mGetMainConn.setRequestProperty("user-agent",mUser_agent);
        }
        mGetMainConn.setRequestProperty("accept",ACCEPT);
        mGetMainConn.setRequestProperty("referer", mUrl);
        if (mCookieList!=null&&mCookieList.size()>0){
            mGetMainConn.setRequestProperty("cookie",listToString(mCookieList));
        }
        mGetMainConn.setUseCaches(false);
        mGetMainConn.connect();
        switch (mGetMainConn.getResponseCode()){
            case HttpURLConnection.HTTP_OK:
                e("MainUrl","visit website success");
                return;
            case HttpURLConnection.HTTP_FORBIDDEN:
                e("MainUrl","IP block or cookie err");
                return;
            case HttpURLConnection.HTTP_UNAVAILABLE:
                InputStream mInputStream = mCheckConn.getErrorStream();
                BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
                StringBuilder sb = new StringBuilder();
                String str;
                while ((str = mBufferedReader.readLine()) != null){
                    sb.append(str);
                }
                mInputStream.close();
                mBufferedReader.close();
                mCookieList = mCookieManager.getCookieStore().getCookies();
                str = sb.toString();
                getCheckAnswer(str);
                break;
            default:

                break;
        }
    }

    /**
     * 获取值并跳转获得cookies
     * @param str
     */
    private void getCheckAnswer(String str) throws InterruptedException, IOException {
        String s = regex(str,"name=\"s\" value=\"(.+?)\"").get(0);   //正则取值
        String jschl_vc = regex(str,"name=\"jschl_vc\" value=\"(.+?)\"").get(0);
        String pass = regex(str,"name=\"pass\" value=\"(.+?)\"").get(0);            //
        double jschl_answer = get_answer(str);
        e(String.valueOf(jschl_answer));
        Thread.sleep(3000);
        String req = String.valueOf("https://"+ConnUrl.getHost())+"/cdn-cgi/l/chk_jschl?";
        if (!TextUtils.isEmpty(s)){
            s = Uri.encode(s);
            req+="s="+s+"&";
        }
        req+="jschl_vc="+Uri.encode(jschl_vc)+"&pass="+Uri.encode(pass)+"&jschl_answer="+jschl_answer;
        e("RedirectUrl",req);
        getRedirectResponse(req);
    }

    private void getRedirectResponse(String req) throws IOException {
        HttpURLConnection.setFollowRedirects(false);
        mGetRedirectionConn = (HttpURLConnection) new URL(req).openConnection();
        mGetRedirectionConn.setRequestMethod("GET");
        mGetRedirectionConn.setConnectTimeout(CONN_TIMEOUT);
        mGetRedirectionConn.setReadTimeout(CONN_TIMEOUT);
        mGetRedirectionConn.setUseCaches(false);
        if (!TextUtils.isEmpty(mUser_agent)){
            mGetRedirectionConn.setRequestProperty("user-agent",mUser_agent);
        }
        mGetRedirectionConn.setRequestProperty("accept",ACCEPT);
        mGetRedirectionConn.setRequestProperty("referer", mUrl);
        if (mCookieList!=null&&mCookieList.size()>0){
            mGetRedirectionConn.setRequestProperty("cookie",listToString(mCookieList));
        }
        mGetRedirectionConn.setUseCaches(false);
        mGetRedirectionConn.connect();
        switch (mGetRedirectionConn.getResponseCode()){
            case HttpURLConnection.HTTP_OK:
                mCookieList = mCookieManager.getCookieStore().getCookies();
                break;
            case HttpURLConnection.HTTP_MOVED_TEMP:
                mCookieList = mCookieManager.getCookieStore().getCookies();
                break;
            default:throw new IOException("getOtherResponse Code: "+
                    mGetRedirectionConn.getResponseCode());
        }
    }


    private int checkUrl()throws IOException {
        URL ConnUrl = new URL(mUrl);
        mCheckConn = (HttpURLConnection) ConnUrl.openConnection();
        mCheckConn.setRequestMethod("GET");
        mCheckConn.setConnectTimeout(CONN_TIMEOUT);
        mCheckConn.setReadTimeout(CONN_TIMEOUT);
        if (!TextUtils.isEmpty(mUser_agent)){
            mCheckConn.setRequestProperty("user-agent",mUser_agent);
        }
        mCheckConn.setRequestProperty("accept",ACCEPT);
        mCheckConn.setRequestProperty("referer",mUrl);
        if (mCookieList!=null&&mCookieList.size()>0){
            mCheckConn.setRequestProperty("cookie",listToString(mCookieList));
        }
        mCheckConn.setUseCaches(false);
        mCheckConn.connect();
        return mCheckConn.getResponseCode();
    }

    private void closeAllConn(){
        if (mCheckConn!=null){
            mCheckConn.disconnect();
        }
        if (mGetMainConn!=null){
            mGetMainConn.disconnect();
        }
        if (mGetRedirectionConn!=null){
            mGetRedirectionConn.disconnect();
        }
    }


    public interface cfCallback{
        void onSuccess(List<HttpCookie> cookieList);
        void onFail();
    }

    private double get_answer(String str) {  //取值
        double a = 0;

        try {
            List<String> s = regex(str,"var s,t,o,p,b,r,e,a,k,i,n,g,f, " +
                    "(.+?)=\\{\"(.+?)\"");
            String varA = s.get(0);
            String varB = s.get(1);
            String div_cfdn = getCfdnDOM(str);
            List<String> eval_fuc = null;
            if (!TextUtils.isEmpty(div_cfdn)){
                eval_fuc = checkEval(str);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("var t=\"").append(new URL(mUrl).getHost()).append("\";");
            sb.append("var a=");
            sb.append(regex(str,varA+"=\\{\""+varB+"\":(.+?)\\}").get(0));
            sb.append(";");
            List<String> b = regex(str,varA+"\\."+varB+"(.+?)\\;");
            if (b != null) {
                for (int i =0;i<b.size()-1;i++){
                    sb.append("a");
                    if (eval_fuc!=null&&eval_fuc.size()>0){
                        sb.append(replaceEval(b.get(i),div_cfdn,eval_fuc));
                    }else {
                        sb.append(b.get(i));
                    }
                    sb.append(";");
                }
            }

            e("add",sb.toString());
            V8 v8 = V8.createV8Runtime();
            a = v8.executeDoubleScript(sb.toString());
            List<String> fixNum = regex(str,"toFixed\\((.+?)\\)");
            if (fixNum!=null){
                e("toFix",fixNum.get(0));
                a = Double.parseDouble(v8.executeStringScript("String("+String.valueOf(a)+".toFixed("+fixNum.get(0)+"));"));
            }
//            a += new URL(mUrl).getHost().length();
            v8.release();
        }catch (IndexOutOfBoundsException e){
            e("answerErr","get answer error");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (V8RuntimeException e){
            e("scriptRuntimeErr","script runtime error,check the js code");
            e.printStackTrace();
        }
        return a;
    }

    private String replaceEval(String s, String div_cfdn, List<String> eval_fuc) {
        List<String> eval = regex(s,"eval\\(eval\\((.+?)");
        if (eval==null||eval.size()==0){
            return s;
        }
        s = s.replace(eval_fuc.get(0),div_cfdn);
        s+=";"+eval_fuc.get(1);
        return s;
    }

    private List<String> checkEval(String str) {
        List<String> evalDom = regex(str,"function\\(p\\)\\{var p = (.+?)\\;(.+?)\\;");
        if (evalDom==null||evalDom.size()==0){
            return null;
        }else {
            return evalDom;
        }
    }

    private String getCfdnDOM(String str) {
        String dom = regex(str,"k \\= \\'(.+?)\\'\\;").get(0);
        if (!TextUtils.isEmpty(dom)){
            String cfdn = regex(str,"id=\""+dom+"\">(.+?)</div>").get(0);
            if (!TextUtils.isEmpty(cfdn)){
                return cfdn;
            }else {
                return "";
            }
        }else {
            return "";
        }
    }

    /**
     * 正则
     * @param text 本体
     * @param pattern 正则式
     * @return List<String>
     */
    private List<String> regex(String text, String pattern){
        try {
            Pattern pt = Pattern.compile(pattern);
            Matcher mt = pt.matcher(text);
            List<String> group = new ArrayList<>();

            while (mt.find()) {
                if (mt.groupCount() >= 1) {
                    if (mt.groupCount()>1){
                        group.add(mt.group(1));
                        group.add(mt.group(2));
                    }else group.add(mt.group(1));
                }
            }
            return group;
        }catch (NullPointerException e){
            Log.i("MATCH","null");
            return null;
        }
    }

    /**
     * 转换list为 ; 符号链接的字符串
     * @param list
     * @return
     */
    public static String listToString(List list ) {
        char separator = ";".charAt(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }


    /**
     * 转换为jsoup可用的Hashmap
     * @param list  HttpCookie列表
     * @return Hashmap
     */
    public static Map<String,String> List2Map(List<HttpCookie> list){
        Map<String, String> map = new HashMap<>();
        try {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    String[] listStr = list.get(i).toString().split("=");
                    map.put(listStr[0], listStr[1]);
                }
                Log.i("List2Map", map.toString());
            } else {
                return map;
            }

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return map;
    }

    private void e(String tag,String content){
        Log.e(tag,content);
    }

    private void e(String content){
        Log.e("cloudflare",content);
    }

}
