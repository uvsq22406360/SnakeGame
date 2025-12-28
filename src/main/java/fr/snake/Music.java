package fr.snake;

import java.io.IOException;

import javax.sound.sampled.*;

public class Music {
	public static void main(String[] args) {
		Music.play("/Sounds/bg.wav", true) ;
	}

	/**
	 * Joue de la musique provenant du fichier donné.
	 * La musique doit être au format .WAV car c'est le seul format audio (et encore utilisé aujourd'hui) que Java
	 * sait lire nativement.
	 * @param filePath chemin du fichier audio
	 * @param isLoop faire une boucle
	 */
	public static void play(final String filePath, final boolean isLoop) {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					final AudioInputStream in = AudioSystem.getAudioInputStream(this.getClass().getResourceAsStream(filePath)) ;
					final AudioFormat outFormat = getOutFormat(in.getFormat()) ;
					final DataLine.Info info = new DataLine.Info(SourceDataLine.class,outFormat) ;
					
					final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info) ;
					
					if (line != null) {
						line.open(outFormat) ;
						line.start() ;
						stream(AudioSystem.getAudioInputStream(outFormat, in), line) ;
						line.drain() ;
						line.stop() ;
						line.close() ;
						if(isLoop){
							play(filePath, isLoop) ;
						}
					}
					
				} catch (LineUnavailableException e) {
					System.err.println("Impossible de jouer de la musique sur ce système") ;
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		});
		t.start();
	}

	private static AudioFormat getOutFormat(AudioFormat inFormat) {
		final int ch = inFormat.getChannels() ;
		final float rate = inFormat.getSampleRate() ;
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch,
				ch * 2, rate, false);
	}

	private static void stream(AudioInputStream in, SourceDataLine line)
			throws IOException {
		final byte[] buffer = new byte[65536] ;
		for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
			line.write(buffer, 0, n) ;
		}
	}
}
