package audiobookplayer.gothamgreensoftware.com.audiobookblankslate;

/**
 * TODO: doc
 * Created by daryachernikhova on 7/11/17.
 */
public class BurgerMenuItem {
  private String iconName;
  private int iconResource;
  private String text;

  public BurgerMenuItem(int iconResource, String text) {
    this.iconResource = iconResource;
    this.text = text;
  }


  public String getIcon() {
    return iconName;
  }

  public void setIcon(String icon) {
    this.iconName = icon;
    // TODO:  and change the objects pulled
  }


  public int getIconResource() {
    return this.iconResource;
  }


  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    // TODO:  and change the objects pulled
  }

}
