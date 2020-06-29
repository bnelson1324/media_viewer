package gui.components.media_display;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import javafx.animation.PauseTransition;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import media_control.MediaHandler;

public class VideoDisplayPanel extends ImageDisplayPanel {

	
	/* !! BUG: display panel is finicky if there is anything in the search query or if there is a selected media item
	 * but if there are neither of these, the display panels appear more consistently, yet still sometimes finicky
	*/
	protected MediaPlayer mp;
	protected MediaView mv;
	
	protected VideoDisplayPanel(Path mediaItem) {
		super(mediaItem, false);
		
		// creates jfxpanel to initialize jfx toolkit
		new JFXPanel();
		
		
			prepareMedia();
			mp.setOnReady( () -> {
				prepareSnapshot();
			});

		
	}
	
	protected void prepareMedia() {
		Media fxMedia = new Media(MediaHandler.getFullRelativePath(mediaItem).toFile().toURI().toString());
		mp = new MediaPlayer(fxMedia);
		mv = new MediaView(mp);
	}
	
	/* !! bugs
	 *
	 * video display panel doesnt properly resize after loading in view/modify tags tab, must manually resize tab to update the panel
	 * also, videos arent being resized at all in the search tab
	 * 
	 * 
	 * also, mp.seek only sometimes works. sometimes it fails and this only shows the thumbnail of the start of the video
	 */
	// prepares thumbnail of video
	protected void prepareSnapshot() {
		// sets media player to the middle of the media
		Duration middleTime = mp.getMedia().getDuration().divide(2);
		mp.seek(middleTime);
		
	
		// delay to let mv prepare for snapshot (i think)
		PauseTransition pt = new PauseTransition(new Duration(50));
		pt.setOnFinished( (e) -> {
			WritableImage wi = new WritableImage(mp.getMedia().getWidth(), mp.getMedia().getHeight());
			mv.snapshot(new SnapshotParameters(), wi);
			
			mediaItemImage = SwingFXUtils.fromFXImage(wi, null);
			imageLabel.setIcon(new ImageIcon(mediaItemImage));
		});
		pt.play();
		
		
		readyToRender = true;
	}
	
	@Override
	public void setDisplaySize(int width, int height, boolean keepAspectRatio) {
		SwingUtilities.invokeLater( () -> {
			super.setDisplaySize(width, height, keepAspectRatio);
		});
		
	}
	
	@Override
	public void addContextMenu() {
		//TODO
	}

}
