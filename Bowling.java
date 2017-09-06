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
		String pinsDownRecord = scoreboard.getFrames().stream()
			.filter(Frame::isFull)
			.map(Frame::printFrame)
			.collect(Collectors.joining("|"));

		String scoresRecord = scoreboard.getPointsList().stream()
			.map(score -> String.format("%3s", score))
			.collect(Collectors.joining("|"));

		return String.format("|%s|\n|%s|", pinsDownRecord, scoresRecord);
	}

}

class Frame {

	Integer[] allBowls;

	public Frame() {
		allBowls = new Integer[2];
	}

	public void update(Integer pinsDown) {
		int index = 0;
		while (index < allBowls.length) {
			if (allBowls[index] == null) {
				allBowls[index] = pinsDown;
				break;
			}
			index++;
		}
	}

	public Integer getTotalPinsDown() {
		return Arrays.stream(allBowls)
			.filter(Objects::nonNull)
			.reduce(0, Integer::sum);
	}
	
	public Integer getPinsDownOnBowl(Integer bowl) {
		return allBowls[bowl - 1];
	}

	public Boolean isFull() {
		return allBowls[0] != null && allBowls[1] != null;
	}
	
	public Boolean isSpare() {
		return getTotalPinsDown() == 10 && getPinsDownOnBowl(1) != 10;
	}

	public Boolean isStrike() {
		return getPinsDownOnBowl(1) == 10;
	}

	public String printFrame() {
		return String.format("%d,%d", allBowls[0], allBowls[1]);
	}
}

class FinalFrame extends Frame {

	public FinalFrame() {
		allBowls = new Integer[3];
	}

	public Integer currentBowl() {
		int index = 0;
		while (index < allBowls.length && allBowls[index] != null) {
			index++;
		}
		return index;
	}

	public Boolean isFull() {
		if (allBowls[0] != null && allBowls[1] != null) {
			if (allBowls[0] + allBowls[1] >= 10) {
				return allBowls[2] != null;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public Boolean isSpare() {
		return !isStrike(1) && (allBowls[0] + allBowls[1] == 10);
	}

	public Boolean isStrike(Integer bowl) {
		return getPinsDownOnBowl(bowl) == 10;
	}

}

class Scoreboard {

	private static final int STRIKE_POINTS = 10;
	private static final int SPARE_POINTS = 10;
	private List<Frame> allFramesList;
	private List<Integer> allPointsList;
	private Integer totalPoints;

	public Scoreboard() {
		totalPoints = 0;
		allFramesList = new ArrayList<>();
		allPointsList = new ArrayList<>();
		addFrame();
	}

	public void update(Integer pinsDown) {
		currentFrame().update(pinsDown);
		updateTotalPoints();

		if (currentFrame().isStrike() && !isFinalFrame()) {
			currentFrame().update(0);
		}

		if (currentFrame().isFull()) {
			allPointsList.add(totalPoints);

			if (!isFinalFrame()) {
				if (allFramesList.size() == 9) {
					addFinalFrame();
				} else {
					addFrame();
				}
			}
		}
	}
	
	public List<Frame> getFrames() {
		return Collections.unmodifiableList(allFramesList);
	}

	public Integer getScore() {
 		return totalPoints;
	}

	public List<Integer> getPointsList() {
		return Collections.unmodifiableList(allPointsList);
	}

	public Boolean isGameOver() {		
		return isFinalFrame() && currentFrame().isFull();
	}
	
	public Boolean isFinalFrame() {
		return allFramesList.size() == 10;
	}
	
	private void addFrame() {
		Frame frame = new Frame();
		allFramesList.add(frame);
	}

	private void addFinalFrame() {
		Frame frame = new FinalFrame();
		allFramesList.add(frame);
	}

	private Frame currentFrame() {
		int lastIndex = allFramesList.size() - 1;
		return allFramesList.get(lastIndex);
	}

	private Optional<Frame> secondToLastFrame() {
		int secondToLastIndex = allFramesList.size() - 2;

		return getFrameByIndex(secondToLastIndex);
	}

	private Optional<Frame> thirdToLastFrame() {
		int thirdToLastIndex = allFramesList.size() - 3;

		return getFrameByIndex(thirdToLastIndex);
	}
	
	private Optional<Frame> getFrameByIndex(int index) {
		if (index >= 0) {
			return Optional.of(allFramesList.get(index));
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
			totalPoints += SPARE_POINTS + currentFrame().getPinsDownOnBowl(1);
		} else if (isTurkey()) {
			totalPoints += STRIKE_POINTS * 3;
		} else if (isDoubleStrike() && currentFrame().isFull()) {
			totalPoints += STRIKE_POINTS * 2 + currentFrame().getPinsDownOnBowl(1);
			totalPoints += STRIKE_POINTS + currentFrame().getTotalPinsDown();
		} else if (isSingleStrike() && currentFrame().isFull()) {
			totalPoints += STRIKE_POINTS + currentFrame().getTotalPinsDown();
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
				} else if (finalFrame.getTotalPinsDown() < 10) {
					totalPoints += finalFrame.getTotalPinsDown();
				}
			} else if (finalFrame.currentBowl() == 3) {
				if (finalFrame.isStrike(1) && finalFrame.isStrike(2) && finalFrame.isStrike(3)) {
					totalPoints += STRIKE_POINTS * 3;
				} else if (finalFrame.isSpare()) {
					totalPoints += finalFrame.getTotalPinsDown();
				}
			}
		} else {
			scoreBonusBowls();
			if (currentFrame().isFull() && !currentFrame().isSpare()) {
				totalPoints += currentFrame().getTotalPinsDown();
			}
		}
	}	
}
