package org.ukfsn.hexis.SteamCommunityApp;

import java.lang.ref.WeakReference;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ListviewImageDownloader {
	
	Net net = new Net();
	
	public void download(String url, ImageView imageView){
		if(cancelPotentialDownload(url, imageView)){
			ImageDownloaderTask task = new ImageDownloaderTask(imageView);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url);
		}
	}
	
	class ImageDownloaderTask extends AsyncTask<String, Void, Drawable>{
		private String url;
		private final WeakReference<ImageView> imageViewReference;
		
		public ImageDownloaderTask(ImageView imageView){
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		@Override
		protected Drawable doInBackground(String... params){
			url = params[0];
			Drawable avatar = net.getImage(url);
			return avatar;
		}
		
		@Override
		protected void onPostExecute(Drawable drawable){
			if(isCancelled()){
				drawable = null;
			}
			
			if(imageViewReference != null){
				ImageView imageView = imageViewReference.get();
				ImageDownloaderTask imageDownloaderTask = getImageDownloaderTask(imageView);
				if(this == imageDownloaderTask){
					imageView.setImageDrawable(drawable);
				}
			}
		}
	}
	
	static class DownloadedDrawable extends ColorDrawable{
		private final WeakReference<ImageDownloaderTask> imageDownloaderTaskReference;
		
		public DownloadedDrawable(ImageDownloaderTask imageDownloaderTask){
			super(Color.BLACK);
			imageDownloaderTaskReference = new WeakReference<ImageDownloaderTask>(imageDownloaderTask);
		}
		
		public ImageDownloaderTask getImageDownloaderTask(){
			return imageDownloaderTaskReference.get();
		}
	}
	
	private static boolean cancelPotentialDownload(String url, ImageView imageView){
		ImageDownloaderTask imageDownloaderTask = getImageDownloaderTask(imageView);
		
		if(imageDownloaderTask != null){
			String drawableURL = imageDownloaderTask.url;
	        if ((drawableURL == null) || (!drawableURL.equals(url))) {
	            imageDownloaderTask.cancel(true);
	        } else {
	            return false;
	        }
	    }
	    return true;
	}
	
	private static ImageDownloaderTask getImageDownloaderTask(ImageView imageView){
		if(imageView != null){
			Drawable drawable = imageView.getDrawable();
			if(drawable instanceof DownloadedDrawable){
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getImageDownloaderTask();
			}
		}
		return null;
	}
}
