package org.ukfsn.hexis.SteamCommunityApp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;

public class Net {
	URL url = null;
	HttpURLConnection urlConnection = null;
	String result = null;
	
	public String getData (String data){
		try {
			getURL(data);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public Drawable getImage (String image){
		Drawable d = ImageOperations(image);
		return d;
	}
	
	public void getURL(String input) throws MalformedURLException, IOException {
		url = new URL(input);
		urlConnection = (HttpURLConnection) url.openConnection();
		InputStream in = new BufferedInputStream(urlConnection.getInputStream());
		result = readStream(in);
		urlConnection.disconnect();
	}
	
	private String readStream (InputStream is) throws IOException{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int i = is.read();
        while(i != -1) {
	        bo.write(i);
	        i = is.read();
        }
        return bo.toString();
	}
	
    private Drawable ImageOperations(String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public Object fetch(String address) throws MalformedURLException,IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }
}
