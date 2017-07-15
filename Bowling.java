import java.util.*;

public class Bowling {

	private Scoreboard scoreboard;

	public Bowling() {
		scoreboard = new Scoreboard();
	}

	public Bowling bowl(Integer pinsDown) {
		if (pinsDown < 0) {
			throw new IllegalArgumentException("Cannot knock down negative pins. Bowl again.");
		}
		scoreboard.update(pinsDown);
		return this;
	}

	public Integer score() {
		return scoreboard.getScore();
	}

}

class Frame {

	private Integer[] frameScores;

	public Frame() {
		frameScores = new Integer[2];
	}

	public void update(Integer pinsDown) {
		if (frameScores[0] == null)
			frameScores[0] = pinsDown;
		else if (frameScores[1] == null)
			frameScores[1] = pinsDown;
	}

	public Integer getFrameScore() {
		Integer totalFrameScore = frameScores[0] + frameScores[1];
		return totalFrameScore;
	}
  
  public Integer firstBowl() {
    return frameScores[0];
  }

	public Boolean isFull() {
		return frameScores[0] != null && frameScores[1] != null ? true : false;
	}

}

class Scoreboard {

	private List<Frame> allFrames;
	private Integer totalPoints;

	public Scoreboard() {
		totalPoints = 0;
		allFrames = new LinkedList<>();
		addFrame();
	}

	public void update(Integer pinsDown) {
		Frame currentFrame = getCurrentFrame();
		currentFrame.update(pinsDown);
		if (currentFrame.isFull()) {
			updateTotalPoints();
			addFrame();
		}
	}

	public void addFrame() {
		Frame frame = new Frame();
		allFrames.add(frame);
	}

	public Frame getCurrentFrame() {
		int lastIndex = allFrames.size() - 1;
		Frame currentFrame = allFrames.get(lastIndex);
		return currentFrame;
	}

	public Integer getScore() {
		return totalPoints;
	}

	public void updateTotalPoints() {
		Integer currentFrameTotal = getCurrentFrame().getFrameScore();
		if (currentFrameTotal != 10)
				totalPoints += currentFrameTotal;
    
    if (allFrames.size() > 1) {
      Integer previousFrameTotal = allFrames.get(allFrames.size()-2).getFrameScore();
      if (previousFrameTotal == 10) {
        totalPoints += previousFrameTotal + getCurrentFrame().firstBowl();
      }
    }
	}

}
