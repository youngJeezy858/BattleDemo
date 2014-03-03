package driver;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;

import com.erebos.engine.core.ECanvas;
import com.erebos.engine.core.EGame;
import com.erebos.engine.graphics.EAnimation;


public class BattleCanvas extends ECanvas {

	private static BattleCanvas c = null;
	
	private EAnimation background;
	private int background_y;
	private int background_x;

	private EAnimation enemy_currentAction;
	private EAnimation enemy_idle;
	private int enemy_x;
	private int enemy_y;
	
	private int enemy_state;
	
	private EAnimation player_currentAction;
	private EAnimation player_idle;
	private EAnimation player_walking;
	private EAnimation player_kick;
	private int player_x;
	private int player_y;
	
	private int player_state;
	public static int PLAYER_READY = 0;
	public static int PLAYER_BUSY = 1;
	
	private int frame_stop;
	private int frame_hit;
	private int[] frame_hit_x;
	private int[] frame_hit_y;
	
	private BattleCanvas(){
		super(1);
	}

	public static BattleCanvas getC() {
		if (c == null)
			c = new BattleCanvas();
		return c;
	}	
	
	@Override
	public void eInit(GameContainer gc, EGame eg) {
		background = new EAnimation(EAnimation.loadImage("/images/TMNT-City.png"));
		
		Image temp = EAnimation.loadImage("/images/player_idle.png");
		SpriteSheet ss = new SpriteSheet(temp, temp.getWidth()/4, temp.getHeight());
		Image tempA[] = {ss.getSprite(0, 0), ss.getSprite(1, 0), ss.getSprite(2, 0), ss.getSprite(3, 0)};
		player_idle = new EAnimation(tempA, new int[]{250,250,250,250});
		
		temp = EAnimation.loadImage("/images/player_walking.png");
		ss = new SpriteSheet(temp, temp.getWidth()/5, temp.getHeight());
		Image tempB[] = {ss.getSprite(0, 0), ss.getSprite(1, 0), ss.getSprite(2, 0), ss.getSprite(3, 0), ss.getSprite(4, 0)};
		player_walking = new EAnimation(tempB, new int[]{150,150,150,150,150});
		
		temp = EAnimation.loadImage("/images/player_kick.png");
		ss = new SpriteSheet(temp, temp.getWidth()/3, temp.getHeight());
		Image tempC[] = {ss.getSprite(0, 0), ss.getSprite(1, 0), ss.getSprite(2, 0), ss.getSprite(2, 0)};
		player_kick = new EAnimation(tempC, new int[]{300,200,200,100});
		
		temp = EAnimation.loadImage("/images/enemy_idle.png");
		ss = new SpriteSheet(temp, temp.getWidth()/2, temp.getHeight());
		Image tempD[] = {ss.getSprite(0, 0), ss.getSprite(1, 0)};
		enemy_idle = new EAnimation(tempD, 100);
		
		
		player_currentAction = player_idle;
		player_x = 400;
		player_y = 400;
		player_state = PLAYER_READY;
		
		enemy_currentAction = enemy_idle;
		enemy_x = 800;
		enemy_y = 400;
		enemy_state = 0;
		
		background_x = 0;
		background_y = 0;
		frame_stop = -1;
		frame_hit = -1;
		frame_hit_x = new int[] {};
		frame_hit_y = new int[] {};
	}

	@Override
	public void eRender(GameContainer gc, EGame eg, Graphics g) {
		background.draw(background_x, background_y);
		enemy_currentAction.getCurrentFrame().draw(enemy_x, enemy_y);
		player_currentAction.getCurrentFrame().draw(player_x, player_y);
	}

	@Override
	public void eUpdate(GameContainer gc, EGame eg, int delta) {
		
		player_currentAction.update(delta);
		Input input = gc.getInput();
			        
   		if (frame_stop != -1 && player_state == PLAYER_BUSY){
   			int currentFrame = player_currentAction.getFrame();
			if (player_currentAction.getFrame() == frame_hit){
				//for all enemies
				for (int i = 0; i < frame_hit_x.length; i++){
					int x = frame_hit_x[i] + player_x - enemy_x;
					int y = frame_hit_y[i] + player_y - enemy_y;
					System.out.println(x + " " + y);
					Image frame = enemy_currentAction.getCurrentFrame();
					int xBound = frame.getWidth();
					int yBound = frame.getHeight();
					if (x < 0 || x > xBound || y < 0 || y > yBound)
						continue;
					Color c = frame.getColor(x, y);
					if (c.a != 0){
						enemy_currentAction.setCurrentFrame(Math.abs(enemy_currentAction.getFrame() - 1));
						break;
					}
				}		
				System.out.println("kick end");
			}
			else if (currentFrame == frame_stop){
				player_state = PLAYER_READY;
			}
			return;
		}
		else
			player_state = PLAYER_READY;
		
		if (input.isKeyPressed(Input.KEY_SPACE)){
			player_state = PLAYER_BUSY;
			player_currentAction = player_kick;
			player_currentAction.setCurrentFrame(0);
			System.out.println("kick begin");
			frame_hit = 1;
			frame_stop = 3;
			frame_hit_x = new int[] {116, 132, 162, 192, 217};
			frame_hit_y = new int[] {94, 74, 52, 27, 25};
		}
		
		else if (input.isKeyDown(Input.KEY_W) || input.isKeyDown(Input.KEY_A)
				|| input.isKeyDown(Input.KEY_S) || input.isKeyDown(Input.KEY_D)) {
			
			player_currentAction = player_walking;
			
			if (input.isKeyDown(Input.KEY_W) && input.isKeyDown(Input.KEY_D)) {
				player_y--;
				player_x++;
			} else if (input.isKeyDown(Input.KEY_W)
					&& input.isKeyDown(Input.KEY_A)) {
				player_y--;
				player_x--;
			} else if (input.isKeyDown(Input.KEY_S)
					&& input.isKeyDown(Input.KEY_D)) {
				player_y++;
				player_x++;
			} else if (input.isKeyDown(Input.KEY_S)
					&& input.isKeyDown(Input.KEY_A)) {
				player_y++;
				player_x--;
			} else if (input.isKeyDown(Input.KEY_A))
				player_x--;
			else if (input.isKeyDown(Input.KEY_D))
				player_x++;
			else if (input.isKeyDown(Input.KEY_S))
				player_y++;
			else if (input.isKeyDown(Input.KEY_W))
				player_y--;
		}
		else{
			frame_stop = -1;
			frame_hit = -1;
			frame_hit_x = new int[] {};
			frame_hit_y = new int[] {};	
			player_currentAction = player_idle;
			player_walking.setCurrentFrame(0);
		}

	}

}
