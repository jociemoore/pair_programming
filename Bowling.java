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
			.map(Frame::printFrame)
			.collect(Collectors.joining("|"));

		String scoresRecord = scoreboard.getFrames().stream()
			.map(frame -> String.format("%3s", frame.printScore()))
			.collect(Collectors.joining("|"));

		return String.format("|%s|\n|%s|", pinsDownRecord, scoresRecord);
	}

}

class Frame {

	List<Integer> allBowls = new ArrayList<>();
	Integer totalScoreAtFrame;

	public void update(Integer pinsDown) {
		allBowls.add(pinsDown);
	}
	
	public void setTotalScoreAtFrame(Integer totalScoreAtFrame) {
		this.totalScoreAtFrame = totalScoreAtFrame;
	}

	public Integer getTotalPinsDown() {
		return allBowls.stream()
			.reduce(0, Integer::sum);
	}
	
	public Integer getPinsDownOnBowl(Integer bowl) {
		return allBowls.get(bowl - 1);
	}

	public Boolean isFull() {
		return allBowls.size() == 2;
	}
	
	public Boolean isSpare() {
		return !isStrike() && getTotalPinsDown() == 10;
	}

	public Boolean isStrike() {
		return getPinsDownOnBowl(1) == 10;
	}

	public String printFrame() {
		if (isStrike()) {
			return " X ";
		} else if (isSpare()) {
			return String.format("%d,/", allBowls.get(0));
		} else {
			return printBasicFrame();
		}
	}
	
	protected String printBasicFrame() {
		String output = allBowls.stream()
			.map(String::valueOf)
			.collect(Collectors.joining(","));

		return isFull() ? output : String.format("%s, ", output);
	}
	
	public String printScore() {
		return totalScoreAtFrame != null ? String.valueOf(totalScoreAtFrame) : "";
	}
}

class FinalFrame extends Frame {

	public Integer currentBowl() {
		return allBowls.size();
	}

	public Boolean isFull() {
		if (allBowls.size() >= 2) {
			if (allBowls.get(0) + allBowls.get(1) >= 10) {
				return allBowls.size() == 3;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public Boolean isSpare() {
		return !isStrike(1) && (allBowls.get(0) + allBowls.get(1) == 10);
	}

	public Boolean isStrike(Integer bowl) {
		return getPinsDownOnBowl(bowl) == 10;
	}

	public String printFrame() {
		List<String> bowlStrings = new ArrayList<>();
		int index = 0;
		while (index < allBowls.size()) {
			if (allBowls.get(index) == 10) {
				bowlStrings.add("X");
			} else if ((index == 1) && ((allBowls.get(index) + allBowls.get(index-1)) == 10)) {
				bowlStrings.add("/");
			} else {
				bowlStrings.add(String.valueOf(allBowls.get(index)));
			}
			index++;
		}
		
		if (!isFull()) {
			bowlStrings.add(" ");
		}
		
		return String.join(",", bowlStrings);
	}
}

class Scoreboard {

	private static final int STRIKE_POINTS = 10;
	private static final int SPARE_POINTS = 10;
	private List<Frame> allFramesList;
	private Integer totalPoints;

	public Scoreboard() {
		totalPoints = 0;
		allFramesList = new ArrayList<>();
		addFrame();
	}

	public void update(Integer pinsDown) {
		currentFrame().update(pinsDown);
		updateTotalPoints();

		if (currentFrame().isStrike() && !isFinalFrame()) {
			currentFrame().update(0);
		}

		if (currentFrame().isFull()) {
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
			secondToLastFrame().get().setTotalScoreAtFrame(totalPoints);
		} else if (isTurkey()) {
			totalPoints += STRIKE_POINTS * 3;
			thirdToLastFrame().get().setTotalScoreAtFrame(totalPoints);
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
				if (secondToLastFrame().get().isStrike()) {
					totalPoints += secondToLastFrame().get().getTotalPinsDown() + finalFrame.getTotalPinsDown();
					secondToLastFrame().get().setTotalScoreAtFrame(totalPoints);
				}
				if (finalFrame.getTotalPinsDown() < 10) {
					totalPoints += finalFrame.getTotalPinsDown();
					currentFrame().setTotalScoreAtFrame(totalPoints);
				}
			} else if (finalFrame.currentBowl() == 3) {
				totalPoints += finalFrame.getTotalPinsDown();
				currentFrame().setTotalScoreAtFrame(totalPoints);
			}
		} else {
			scoreBonusBowls();
			if (currentFrame().isFull() && !currentFrame().isSpare()) {
				totalPoints += currentFrame().getTotalPinsDown();
				currentFrame().setTotalScoreAtFrame(totalPoints);
			}
		}
	}	
}
