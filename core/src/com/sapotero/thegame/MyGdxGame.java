package com.sapotero.thegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;

  private Sound dropSound;
	private Music rainMusic;

	private OrthographicCamera camera;

  private SpriteBatch batch;

  private Rectangle bucket;
  private Array<Rectangle> raindrops;
  private long lastDropTime;

  private int WIDTH  = 1920;
  private int HEIGHT = 1080;
  private int SPRITE = 256;

	@Override
	public void create() {

    batch = new SpriteBatch();

		dropImage   = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		rainMusic.setLooping(true);
		rainMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);


    bucket = new Rectangle();
    bucket.x = WIDTH / 2 - SPRITE / 2;
    bucket.y = SPRITE;
    bucket.width = SPRITE;
    bucket.height = SPRITE;

    raindrops = new Array<Rectangle>();
    spawnRaindrop();

	}

	@Override
	public void render () {
    Gdx.gl.glClearColor(0, 0, 0.2f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    camera.update();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.draw(bucketImage, bucket.x, bucket.y);
    batch.end();


    if(Gdx.input.isTouched()) {
      Vector3 touchPos = new Vector3();
      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
      camera.unproject(touchPos);
      bucket.x = touchPos.x - SPRITE / 2;
      bucket.y = touchPos.y - SPRITE / 2;
    }

    if(bucket.x < 0) bucket.x = 0;
    if(bucket.x > WIDTH - SPRITE) bucket.x = WIDTH - SPRITE;
    if(bucket.y < 0) bucket.y = 0;
    if(bucket.y > HEIGHT - SPRITE) bucket.y = HEIGHT - SPRITE;

    if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

    Iterator<Rectangle> iter = raindrops.iterator();
    while(iter.hasNext()) {
      Rectangle raindrop = iter.next();
      raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
      if(raindrop.y + SPRITE < 0) iter.remove();

      if(raindrop.overlaps(bucket)) {
        dropSound.play();
        iter.remove();
      }
    }

    batch.begin();
    batch.draw(bucketImage, bucket.x, bucket.y);
    for(Rectangle raindrop: raindrops) {
      batch.draw(dropImage, raindrop.x, raindrop.y);
    }
    batch.end();
	}

  private void spawnRaindrop() {
    Rectangle raindrop = new Rectangle();
    raindrop.x = MathUtils.random(0, WIDTH-SPRITE);
    raindrop.y = HEIGHT;
    raindrop.width = SPRITE;
    raindrop.height = SPRITE;
    raindrops.add(raindrop);
    lastDropTime = TimeUtils.nanoTime();
  }

  @Override
  public void dispose() {
    dropImage.dispose();
    bucketImage.dispose();
    dropSound.dispose();
    rainMusic.dispose();
    batch.dispose();
  }
}
