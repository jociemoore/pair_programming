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

	private Integer[] frameScores;
	private Integer currentBall;

	public Frame() {
		frameScores = new Integer[2];
	}
	
	public Frame(boolean finalFrame) {
		if (finalFrame) {
			frameScores = new Integer[3];
		} else {
			frameScores = new Integer[2];
		}
	}

	public void update(Integer pinsDown) {
		int index = 0;
		while (index < frameScores.length) {
			if (frameScores[index] == null) {
				frameScores[index] = pinsDown;
				currentBall = index + 1;
				break;
			}
			index++;
		}
	}

	public Integer getFrameScore() {
		Integer totalFrameScore = frameScores[0] + frameScores[1];
		return totalFrameScore;
	}

	public Integer currentBall() {
		return currentBall;
	}
	
	public Integer firstBowl() {
		return frameScores[0];
	}

	public Integer secondBowl() {
		return frameScores[1];
	}

	public Integer thirdBowl() {
		return frameScores[2];
	}		

	public Boolean isFull() {
		if (frameScores.length == 2) {
			return frameScores[0] != null && frameScores[1] != null;
		} else if (frameScores.length == 3) {
			if (frameScores[0] != null && frameScores[1] != null) {
				if (frameScores[0] + frameScores[1] >= 10) {
					return frameScores[2] != null;
				} else {
					return true;
				}
			}
		}

		return false;
	}
	
	public Boolean isSpare() {
		return getFrameScore() == 10 && firstBowl() != 10;
	}

	public Boolean isStrike() {
		return firstBowl() == 10;
	}
	
	public String printFrame() {
		return String.format("[%d,%d]", frameScores[0], frameScores[1]);
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
				addFrame(true);
			} else {
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
		return allFrames.size() == 11;
	}
	
	public Boolean isFinalFrame() {
		return allFrames.size() >= 10;
	}
	
	private void addFrame() {
		addFrame(false);
	}
	
	private void addFrame(boolean finalFrame) {
		Frame frame = new Frame(finalFrame);
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
			totalPoints += SPARE_POINTS + currentFrame().firstBowl();
		} else if (isTurkey()) {
			totalPoints += STRIKE_POINTS * 3;
		} else if (isDoubleStrike() && currentFrame().isFull()) {
			totalPoints += STRIKE_POINTS * 2 + currentFrame().firstBowl();
			totalPoints += STRIKE_POINTS + currentFrame().getFrameScore();
		} else if (isSingleStrike() && currentFrame().isFull()) {
			totalPoints += STRIKE_POINTS + currentFrame().getFrameScore();
		}
	}

	private void updateTotalPoints() {
		if (isFinalFrame()) {
			if (currentFrame().currentBall() == 1) {
				scoreBonusBowls();
			} else if (currentFrame().currentBall() == 2) {
				if (secondToLastFrame().get().isStrike() && currentFrame().firstBowl() == 10 && currentFrame().secondBowl() == 10) {
					totalPoints += STRIKE_POINTS * 3;
				} else if (secondToLastFrame().get().isStrike() && currentFrame().firstBowl() == 10 && currentFrame().secondBowl() < 10) {
					totalPoints += STRIKE_POINTS * 2 + currentFrame().secondBowl();
				} else if (currentFrame().firstBowl() < 10 && currentFrame().secondBowl() < 10) {
					totalPoints += currentFrame().getFrameScore();
				}
			} else if (currentFrame().currentBall() == 3) {
				if (currentFrame().firstBowl() == 10 && currentFrame().secondBowl() < 10) {
					totalPoints += STRIKE_POINTS + currentFrame().secondBowl() + currentFrame().thirdBowl();
				}

				if (currentFrame().firstBowl() == 10 && currentFrame().secondBowl() == 10 && currentFrame().thirdBowl() == 10) {
					totalPoints += STRIKE_POINTS * 3;
				} else if (currentFrame().firstBowl() == 10 && currentFrame().secondBowl() == 10 && currentFrame().thirdBowl() < 10) {
					totalPoints += STRIKE_POINTS * 2 + currentFrame().thirdBowl();
				} else if (currentFrame().secondBowl() == 10 && currentFrame().thirdBowl() < 10) {
					totalPoints += STRIKE_POINTS + currentFrame().thirdBowl();
				} else if (currentFrame().firstBowl() + currentFrame().secondBowl() == 10) {
					totalPoints += SPARE_POINTS + currentFrame().thirdBowl();
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
