import java.util.*;

public class Bowling {

  private Scoreboard scoreboard;

  public Bowling() {
    scoreboard = new Scoreboard();
    scoreboard.addFrame();
  }

  public Bowling bowl(Integer pinsDown) {
    if (pinsDown < 0) {
      throw new IllegalArgumentException("Cannot knock down negative pins. Bowl again.");
    }

    Frame currentFrame = scoreboard.getCurrentFrame();
    currentFrame.update(pinsDown);

    if (currentFrame.isFull()) {
        scoreboard.updateScore();
        scoreboard.addFrame();
    }

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

  public Boolean isFull() {
    return frameScores[0] != null && frameScores[1] != null ? true : false;
  }

}

class Scoreboard {

  private LinkedList<Frame> allFrames;
  private Integer totalPoints;

  public Scoreboard() {
    totalPoints = 0;
    allFrames = new LinkedList<>();
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

  public void updateScore() {
    Integer currentFrameTotal = getCurrentFrame().getFrameScore();
    if (currentFrameTotal != 10) {
        totalPoints += currentFrameTotal;
    }
  }

}
