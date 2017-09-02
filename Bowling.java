import java.util.*;
import java.util.stream.Collectors;

public class Bowling {

	private Scoreboard scoreboard;

	public Bowling() {
		scoreboard = new Scoreboard();
	}

	public Bowling bowl(Integer pinsDown) {
		if (scoreboard.isGameOver()) {
			throw new IllegalArgumentException("You cannot bowl more in a completed game.");
		} else if (pinsDown < 0) {
			throw new IllegalArgumentException("Cannot knock down negative pins. Bowl again.");
		}
		scoreboard.update(pinsDown);
		return this;
	}

	public Integer score() {
		return scoreboard.getScore();
	}

	public String printScoreboard() {
		return scoreboard.getFrames().stream()
			.filter(Frame::isFull)
			.map(Frame::printFrame)
			.collect(Collectors.joining(","));
	}

}

class Frame {

	protected Integer[] frameScores;

	public Frame() {
		frameScores = new Integer[2];
	}

	public void update(Integer pinsDown) {
		int index = 0;
		while (index < frameScores.length) {
			if (frameScores[index] == null) {
				frameScores[index] = pinsDown;
				break;
			}
			index++;
		}
	}

	public Integer getFrameScore() {
		Integer totalFrameScore = frameScores[0] + frameScores[1];
		return totalFrameScore;
	}
	
	public Integer getBowl(Integer bowl) {
		bowl -= 1;
		return frameScores[bowl];
	}

	public Boolean isFull() {
		return frameScores[0] != null && frameScores[1] != null;
	}
	
	public Boolean isSpare() {
		return getFrameScore() == 10 && getBowl(1) != 10;
	}

	public Boolean isStrike() {
		return getBowl(1) == 10;
	}

	public String printFrame() {
		return String.format("[%d,%d]", frameScores[0], frameScores[1]);
	}
}

class FinalFrame extends Frame {

	public FinalFrame() {
		frameScores = new Integer[3];
	}

	public Integer currentBowl() {
		int index = 0;
		while (index < frameScores.length && frameScores[index] != null) {
			index++;
		}
		return index;
	}	

	public Boolean isFull() {
		if (frameScores[0] != null && frameScores[1] != null) {
			if (frameScores[0] + frameScores[1] >= 10) {
				return frameScores[2] != null;
			} else {
				return true;
			}
		}
		return false;
	}

	public Boolean isStrike(Integer bowl) {
		return getBowl(bowl) == 10;
	}

}

class Scoreboard {

	private static final int STRIKE_POINTS = 10;
	private static final int SPARE_POINTS = 10;
	private List<Frame> allFrames;
	private Integer totalPoints;

	public Scoreboard() {
		totalPoints = 0;
		allFrames = new ArrayList<>();
		addFrame();
	}

	public void update(Integer pinsDown) {
		currentFrame().update(pinsDown);
		updateTotalPoints();

		if (currentFrame().isStrike() && !isFinalFrame()) {
			currentFrame().update(0);
		}

		if (currentFrame().isFull()) {
			
			if (allFrames.size() == 9) {
				addFinalFrame();
			} else if (allFrames.size() < 9) {
				addFrame();
			}
		}
	}
	
	public List<Frame> getFrames() {
		return Collections.unmodifiableList(allFrames);
	}

	public Integer getScore() {
		return totalPoints;
	}

	public Boolean isGameOver() {		
		return isFinalFrame() && currentFrame().isFull();
	}
	
	public Boolean isFinalFrame() {
		return currentFrame() instanceof FinalFrame;
	}
	
	private void addFrame() {
		Frame frame = new Frame();
		allFrames.add(frame);
	}

	private void addFinalFrame() {
		Frame frame = new FinalFrame();
		allFrames.add(frame);
	}

	private Frame currentFrame() {
		int lastIndex = allFrames.size() - 1;
		return allFrames.get(lastIndex);
	}

	private Optional<Frame> secondToLastFrame() {
		int nextToLastIndex = allFrames.size() - 2;

		if (allFrames.size() > 1) {
			return Optional.of(allFrames.get(nextToLastIndex));
		}
		
		return Optional.empty();
	}

	private Optional<Frame> thirdToLastFrame() {
		int thirdToLastIndex = allFrames.size() - 3;

		if (allFrames.size() > 2) {
			return Optional.of(allFrames.get(thirdToLastIndex));
		}
		
		return Optional.empty();
	}

	private Boolean isTurkey() {
		return currentFrame().isStrike() &&
			   secondToLastFrame().isPresent() && 
			   secondToLastFrame().get().isStrike() && 
			   thirdToLastFrame().isPresent() && 
			   thirdToLastFrame().get().isStrike();
	}

	private Boolean isDoubleStrike() {
		return secondToLastFrame().isPresent() && 
			   secondToLastFrame().get().isStrike() && 
			   thirdToLastFrame().isPresent() && 
			   thirdToLastFrame().get().isStrike();
	}

	private Boolean isSingleStrike() {
		return secondToLastFrame().isPresent() && 
		       secondToLastFrame().get().isStrike();
	}

	private void scoreBonusBowls() {
		if (secondToLastFrame().isPresent() && secondToLastFrame().get().isSpare() && !currentFrame().isFull()) {
			totalPoints += SPARE_POINTS + currentFrame().getBowl(1);
		} else if (isTurkey()) {
			totalPoints += STRIKE_POINTS * 3;
		} else if (isDoubleStrike() && currentFrame().isFull()) {
			totalPoints += STRIKE_POINTS * 2 + currentFrame().getBowl(1);
			totalPoints += STRIKE_POINTS + currentFrame().getFrameScore();
		} else if (isSingleStrike() && currentFrame().isFull()) {
			totalPoints += STRIKE_POINTS + currentFrame().getFrameScore();
		}
	}

	private void updateTotalPoints() {
		if (isFinalFrame()) {
			FinalFrame finalFrame = (FinalFrame) currentFrame();
			if (finalFrame.currentBowl() == 1) {
				scoreBonusBowls();
			} else if (finalFrame.currentBowl() == 2) {
				if (secondToLastFrame().get().isStrike() && finalFrame.isStrike(1) && finalFrame.isStrike(2)) {
					totalPoints += STRIKE_POINTS * 3;
				}
			} else if (finalFrame.currentBowl() == 3) {
				if (finalFrame.isStrike(1) && finalFrame.isStrike(2) && finalFrame.isStrike(3)) {
					totalPoints += STRIKE_POINTS * 3;
				}
			}
		} else {
			scoreBonusBowls();
			if (currentFrame().isFull() && !currentFrame().isSpare()) {
				totalPoints += currentFrame().getFrameScore();
			}
		}
	}	

}
