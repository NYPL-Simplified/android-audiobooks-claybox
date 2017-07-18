package audiobookplayer.gothamgreensoftware.com.audiobookblankslate;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

import android.media.AudioManager;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;


/**
 * TODO doc
 *
 * TODO:  MediaPlayer needs android.permission.WAKE_LOCK permission.  write code to fail gracefully if it's not there.
 *
 * Created by daryachernikhova on 7/12/17.
 */
public class AudioService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

  // so can filter all log msgs belonging to my app
  private final String TAG = "AudioBS.AudioService";
  // so can do a search in log msgs for just this class's output
  //private final String SUB_TAG = "AudioService";


  // media player
  private MediaPlayer mediaPlayer;
  // TODO: maybe want to make it a List, ArrayList seems specific
  // chapter list (queue)
  private ArrayList<ChapterTrack> chapters = null;
  // current position in chapter track queue
  private int chapterPosition;

  // instance of the inner binder class
  private final IBinder musicBinder = new MusicBinder();


  /**
   * TODO doc
   */
  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "onCreate");

    chapterPosition = 0;

    mediaPlayer = new MediaPlayer();
    initializePlayer();
  }


  public void initializePlayer() {
    Log.d(TAG, "initializePlayer");

    // let playback continue when the device becomes idle
    mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    // must set setOnPreparedListener so we can call media playback asynchronously, so's not to hang the UI thread
    /* old code with standalone player
    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
      @Override
      public void onPrepared(MediaPlayer mp) {
        mp.start();
      }
    });
    */

    // new code lets the AudioService handle MediaPlayer events
    // corresponds to the interfaces AudioService implements
    mediaPlayer.setOnPreparedListener(this);
    mediaPlayer.setOnCompletionListener(this);
    mediaPlayer.setOnErrorListener(this);
  }


  /**
   * Called when a client component (an activity) binds to the Service for the first time through bindService().
   * Returns an inner class IBinder interface implementation.
   * Calling activity accepts this IBinder through its ServiceConnection interface, which it has to supply while binding.
   * The IBinder is then used as the communication channel between this service and the client component (the calling activity).
   * The component invokes methods of the communication interface and the execution happens in the Service.
   *
   * NOTE: System calls Service's onBind() only once when the first client binds to retrieve the IBinder.
   * The system then won't call onBind() again when any additional clients bind.  Instead it'll deliver the same IBinder.
   *
   * @param intent
   * @return MusicBinder to communicate through
   */
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Log.d(TAG, "onBind");
    return musicBinder;
  }


  /**
   * Is called when ALL bindings are unbound.
   * Release MediaPlayer resources when unbound from calling activity
   * (when user exists the app).
   *
   * @param intent
   * @return
   */
  @Override
  public boolean onUnbind(Intent intent) {
    Log.d(TAG, "onUnbind");
    mediaPlayer.stop();
    mediaPlayer.release();
    return false;
  }


  /**
   * Called when MediaPlayer finishes playing the audio it was given.
   *
   * @param mp
   */
  @Override
  public void onCompletion(MediaPlayer mp) {
    Log.d(TAG, "onCompletion");
  }


  /**
   * Invoked when there has been an error during an asynchronous operation (other errors will throw exceptions at method call time)
   *
   * @param mp
   * @param what
   * @param extra
   * @return
   */
  @Override
  public boolean onError(MediaPlayer mp, int what, int extra) {
    Log.d(TAG, "onError");
    return false;
  }


  /**
   * TODO:  doc
   *
   * @param mp
   */
  @Override
  public void onPrepared(MediaPlayer mp) {
    Log.d(TAG, "onPrepared");
    // TODO:  need error handling here?
    mediaPlayer.start();
  }


  /**
   * TODO:  doc
   *
   * @param intent
   * @param flags
   * @param startId
   * @return
   */
  @Override
  //public int onStartCommand(Intent intent, @IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true) int flags, int startId) {
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }


  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
  }

  public void setChapters(ArrayList<ChapterTrack> chapters) {
    this.chapters = chapters;
  }

  public void setChapterPosition(int chapterPosition) {
    this.chapterPosition = chapterPosition;
  }



  /**
   * TODO:  must wrap MediaPlayer in my own implementation to keep track of state when the
   * activity that starts the player stops and resumes (https://stackoverflow.com/questions/11876229/android-mediaplayer-is-there-an-isprepared-or-getstatus-method).
   */
  public void playTrack() {
    Log.d(TAG, "playTrack");

    // TODO: need this?  maybe, if am switching a book, but maybe restrict when it's called
    // unsets data source and un-prepares
    mediaPlayer.reset();

    // get track to play
    try {
      ChapterTrack playChapter = chapters.get(chapterPosition);
    } catch (Exception e) {
      Log.d("", "");
    }

    /* TODO: this code is from the tutorial, and needs to be meshed with how I'm doing things
    for now, I'll keep my hardcoded test code

    // get its id
    long currSong = playChapter.getID();
    // set uri
    Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
    */

    // TODO remove mp3 file from res/raw, we don't need it for testing anymore

    // TODO mp3 file location is currently hardcoded to assets dir, but in SimplyE will be calling a URI that's outside of the apk
    String chapterFilePath = "21_gun_salute/1752599_001_C001.mp3";

    //Uri myUri = Uri.parse("file:///android_asset/a21_gun_salute/a1752599_001_c001.mp3"); // initialize Uri here

    AssetManager assetManager = getResources().getAssets();
    AssetFileDescriptor assetFileDescriptor = null;

    /*
    moving to AudioService class
    MediaPlayer mediaPlayer = new MediaPlayer();
    // so we can call media playback asynchronously, so's not to hang the UI thread
    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
      @Override
      public void onPrepared(MediaPlayer mp) {
        mp.start();
      }
    });
    */

    try {
      assetFileDescriptor = assetManager.openFd(chapterFilePath);
      //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());

      //mediaPlayer.prepare(); // synchronously
      mediaPlayer.prepareAsync();
    } catch (IOException e) {
      // TODO
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      // TODO
      e.printStackTrace();
    } finally {
      if (assetFileDescriptor != null) {
        try {
          assetFileDescriptor.close();
        } catch (IOException e) {
          // TODO
          e.printStackTrace();
        }
      }
    }

    mediaPlayer.start();
  }


  /**
   * IBinder interface implementation, to use as the communication channel between this service and the client components.
   * The components invoke methods of the IBinder (?TODO: check) and the execution happens in the Service.
   * Is a communication interface to send requests and receive responses either within a particular process or across processes.
   */
  public class MusicBinder extends Binder {
    AudioService getService() {
      Log.d(TAG, "MusicBinder.getService");

      return AudioService.this;
    }
  }
}
