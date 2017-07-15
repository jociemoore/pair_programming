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

	public Integer getScore() {
		return totalPoints;
	}

	private void addFrame() {
		Frame frame = new Frame();
		allFrames.add(frame);
	}

	private Frame getCurrentFrame() {
		int lastIndex = allFrames.size() - 1;
		Frame currentFrame = allFrames.get(lastIndex);
		return currentFrame;
	}
  
  private Optional<Frame> previousFrame() {
    if (allFrames.size() > 1) {
      int nextToLastIndex = allFrames.size() - 2;
      return Optional.of(allFrames.get(nextToLastIndex));
    }
    
    return Optional.empty();
  }

	private void updateTotalPoints() {
		Integer currentFrameTotal = getCurrentFrame().getFrameScore();
		if (currentFrameTotal != 10)
				totalPoints += currentFrameTotal;
    
    Optional<Frame> previousFrame = previousFrame();
    if (previousFrame.isPresent()) {
      Integer previousFrameTotal = previousFrame.get().getFrameScore();

      if (previousFrameTotal == 10) {
        totalPoints += previousFrameTotal + getCurrentFrame().firstBowl();
      }
    }
	}

}
