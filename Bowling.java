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

	public Frame() {
		frameScores = new Integer[2];
	}

	public void update(Integer pinsDown) {
		if (frameScores[0] == null) {
			frameScores[0] = pinsDown;
		}
		else if (frameScores[1] == null) {
			frameScores[1] = pinsDown;
		}
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
	private Optional<Frame> secondToLastFrame;
	private Optional<Frame> thirdToLastFrame;
	private Frame currentFrame;

	public Scoreboard() {
		totalPoints = 0;
		allFrames = new ArrayList<>();
		addFrame();
	}

	public void update(Integer pinsDown) {
		currentFrame.update(pinsDown);

		if (currentFrame.isStrike()) {
			currentFrame.update(0);
		}

		if (currentFrame.isFull()) {
			updateTotalPoints();
			addFrame();
		}
	}
	
	public List<Frame> getFrames() {
		return Collections.unmodifiableList(allFrames);
	}

	public Integer getScore() {
		return totalPoints;
	}

	public Boolean isGameOver() {
		currentFrame = getCurrentFrame();
		secondToLastFrame = getFrameBefore(currentFrame);
		if (secondToLastFrame.isPresent()) {
			thirdToLastFrame = getFrameBefore(secondToLastFrame.get());
		}
		
		return allFrames.size() == 11 && 
			   !secondToLastFrame.get().isStrike() && 
			   !secondToLastFrame.get().isSpare();
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
	
	private Optional<Frame> getFrameBefore(Frame frame) {
		int currentIndex = allFrames.indexOf(frame);
		int lastIndex = allFrames.size() - 1;
		int nextToLastIndex = allFrames.size() - 2;
		int thirdToLastIndex = allFrames.size() - 3;

		if (allFrames.size() > 1 && currentIndex == lastIndex) {
			return Optional.of(allFrames.get(nextToLastIndex));
		} else if (allFrames.size() > 2 && currentIndex == nextToLastIndex) {
			return Optional.of(allFrames.get(thirdToLastIndex));
		}
		
		return Optional.empty();
	}

	private Boolean isTurkey() {
		return currentFrame.isStrike() &&
			   secondToLastFrame.isPresent() && 
			   secondToLastFrame.get().isStrike() && 
			   thirdToLastFrame.isPresent() && 
			   thirdToLastFrame.get().isStrike();
	}

	private Boolean isDoubleStrike() {
		return secondToLastFrame.isPresent() && 
			   secondToLastFrame.get().isStrike() && 
			   thirdToLastFrame.isPresent() && 
			   thirdToLastFrame.get().isStrike();
	}

	private Boolean isSingleStrike() {
		return secondToLastFrame.isPresent() && 
		       secondToLastFrame.get().isStrike() && 
		       !currentFrame.isStrike() &&
					 (!thirdToLastFrame.isPresent() ||
					   thirdToLastFrame.isPresent() && !thirdToLastFrame.get().isStrike());
	}

	private void updateTotalPoints() {
		if (!currentFrame.isSpare() && !currentFrame.isStrike()) {
			totalPoints += currentFrame.getFrameScore();
		}

		if (secondToLastFrame.isPresent() && secondToLastFrame.get().isSpare()) {
			totalPoints += SPARE_POINTS + getCurrentFrame().firstBowl();
		} else if (isTurkey()) {
			totalPoints += STRIKE_POINTS * 3;
		} else if (isDoubleStrike()) {
			totalPoints += STRIKE_POINTS * 2 + getCurrentFrame().firstBowl();
			totalPoints += STRIKE_POINTS + getCurrentFrame().getFrameScore();
		} else if (isSingleStrike()) {
			totalPoints += STRIKE_POINTS + getCurrentFrame().getFrameScore();
		}
	}

}
