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
		String pinsDown = scoreboard.getFrames().stream()
			.filter(Frame::isFull)
			.map(Frame::printFrame)
			.collect(Collectors.joining("|"));

		String scores = scoreboard.getAllTotals().stream()
			.map(s -> String.format("%3s", s))
			.collect(Collectors.joining("|"));

		return String.format("|%s|\n|%s|", pinsDown, scores);
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
		return String.format("%d,%d", frameScores[0], frameScores[1]);
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
	
	public Integer getFrameScore() {
		Integer totalFrameScore = frameScores[0] + frameScores[1];
		if (frameScores[2] != null) {
			totalFrameScore += frameScores[2];
		}
		return totalFrameScore;
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
	
	public Boolean isSpare() {
		return !isStrike(1) && (frameScores[0] + frameScores[1] == 10);
	}

	public Boolean isStrike(Integer bowl) {
		return getBowl(bowl) == 10;
	}

}

class Scoreboard {

	private static final int STRIKE_POINTS = 10;
	private static final int SPARE_POINTS = 10;
	private List<Frame> allFrames;
	private List<Integer> allTotals;
	private Integer totalPoints;

	public Scoreboard() {
		totalPoints = 0;
		allFrames = new ArrayList<>();
		allTotals = new ArrayList<>();
		addFrame();
	}

	public void update(Integer pinsDown) {
		currentFrame().update(pinsDown);
		updateTotalPoints();

		if (currentFrame().isStrike() && !isFinalFrame()) {
			currentFrame().update(0);
		}

		if (currentFrame().isFull()) {
			allTotals.add(totalPoints);

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

	public List<Integer> getAllTotals() {
		return Collections.unmodifiableList(allTotals);
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
		int secondToLastIndex = allFrames.size() - 2;

		return getFrameByIndex(secondToLastIndex);
	}

	private Optional<Frame> thirdToLastFrame() {
		int thirdToLastIndex = allFrames.size() - 3;

		return getFrameByIndex(thirdToLastIndex);
	}
	
	private Optional<Frame> getFrameByIndex(int index) {
		if (index >= 0) {
			return Optional.of(allFrames.get(index));
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
				} else if (finalFrame.getFrameScore() < 10) {
					totalPoints += finalFrame.getFrameScore();
				}
			} else if (finalFrame.currentBowl() == 3) {
				if (finalFrame.isStrike(1) && finalFrame.isStrike(2) && finalFrame.isStrike(3)) {
					totalPoints += STRIKE_POINTS * 3;
				} else if (finalFrame.isSpare()) {
					totalPoints += finalFrame.getFrameScore();
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
