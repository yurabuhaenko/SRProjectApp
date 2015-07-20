package denver.srprojectapp;


        import java.io.IOException;
        import java.io.UnsupportedEncodingException;
        import java.util.List;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.client.methods.HttpPut;
        import org.apache.http.client.methods.HttpDelete;
        import org.apache.http.client.utils.URLEncodedUtils;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.util.EntityUtils;

public class ServiceServerHandler {

    static String response = null;
    public final static String GET = "GET";
    public final static String POST = "POST";
    public final static String PUT = "PUT";
    public final static String DELETE = "DELETE";


    public static final String TAG_ERROR = "error";

    public ServiceServerHandler() {

    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, String method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * @apiKey - user api key
     * */

    public String makeServiceCall(String url, String method,
                                  List<NameValuePair> params){
        return this.makeServiceCall(url, method, params, null);
    }


    public String makeServiceCall(String url, String method,
                                  List<NameValuePair> params, String apiKey) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params

                //'Authorization'
                if (apiKey != null){
                    httpPost.setHeader("Authorization", apiKey);
                }

                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                HttpGet httpGet = new HttpGet(url);
                if (apiKey != null){
                    httpGet.setHeader("Authorization", apiKey);
                }

                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }


                httpResponse = httpClient.execute(httpGet);

            }
            else if (method == PUT) {
                // appending params to url
                HttpPut httpPut = new HttpPut(url);
                if (apiKey != null){
                    httpPut.setHeader("Authorization", apiKey);
                }

                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }


                httpResponse = httpClient.execute(httpPut);

            }




            ////create DELETE






            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }
}