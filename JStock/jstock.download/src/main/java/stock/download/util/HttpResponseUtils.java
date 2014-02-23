package stock.download.util;

import org.apache.http.HttpResponse;

public final class HttpResponseUtils {
	private HttpResponseUtils(){
		
	}
	
	public static int responseCode(HttpResponse response) {
		return response.getStatusLine().getStatusCode();
	}
	
	public static boolean passed(HttpResponse response) {
		int sc = responseCode(response);
		return sc >= 200 && sc < 300;
	}
}