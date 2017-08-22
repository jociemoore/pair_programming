import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Ignore;

public class BowlingTest {

	private Bowling bowling;

	@Before 
	public void setup() {
		bowling = new Bowling();
	}

	@Test
	public void shouldOutputAScoreGivenOneFrame() throws Exception {
		bowling.bowl(2);
		bowling.bowl(3);
		int score = bowling.score();
		assertEquals(5, score);
	}

	@Test
	public void shouldOutputScoreForEmptyGame() throws Exception {
		bowling.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0)
			.bowl(0).bowl(0);

		int score = bowling.score();
		assertEquals(0, score);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenPinsDownIsNegative() throws Exception {
		try {
			bowling.bowl(-1);
			fail("Illegal Argument Exception was not thrown for bowling negative pins.");
		} catch (IllegalArgumentException iae) {
			String message = "Cannot knock down negative pins. Bowl again.";
			assertEquals(message, iae.getMessage());
		}
	}
		
	@Test
	public void shouldNotTotalScoreForAFrameWithASpare() throws Exception {
		bowling.bowl(5).bowl(5);
		
		int score = bowling.score();
		
		assertEquals(0, score);
	}

	@Test
	public void shouldOutputScoreOfPreviousFramesAfterASpare() throws Exception {
		bowling.bowl(2).bowl(7)
			.bowl(4).bowl(2)
			.bowl(5).bowl(5);

		int score = bowling.score();

		assertEquals(15, score);
	}

	@Test
	public void shouldIncludeSpareScoreAfterNextNonSpareNonStrikeFrame() throws Exception {
		bowling.bowl(3).bowl(5)
			.bowl(3).bowl(7)
			.bowl(4).bowl(0);

		int score = bowling.score();

		assertEquals(26, score);
	}
	
	@Test
	public void shouldOutputScoreOfPreviousFramesAfterAStrike() throws Exception {
		bowling.bowl(4).bowl(2)
			.bowl(10);
		
		int score = bowling.score();
		
		assertEquals(6, score);
	}
	
	@Test
	public void shouldScoreStrikeFrameAfterNextTwoBallsForSingleFrame() throws Exception {
		bowling.bowl(4).bowl(2)
			.bowl(10)
			.bowl(4).bowl(2);
		
		int score = bowling.score();
		
		assertEquals(28, score);
	}
	
	@Test
	public void shouldScoreStrikeAfterNextTwoStrikes() throws Exception {
		bowling.bowl(10)
			.bowl(10)
			.bowl(10);
		
		int score = bowling.score();
		
		assertEquals(30, score);
	}

	@Test
	public void shouldScoreTwoStrikesAfterNonSpareNonStrikeFrame() throws Exception {
		bowling.bowl(10)
			.bowl(10)
			.bowl(5)
			.bowl(0);
		
		int score = bowling.score();
		
		assertEquals(45, score);
	}
	
	@Test
	public void shouldPreventRecordingExtraBowlsAtEndOfGame() throws Exception {
		bowling.bowl(1).bowl(2)
			.bowl(3).bowl(4)
			.bowl(5).bowl(4)
			.bowl(3).bowl(2)
			.bowl(1).bowl(0)
			.bowl(1).bowl(2)
			.bowl(3).bowl(4)
			.bowl(5).bowl(4)
			.bowl(3).bowl(2)
			.bowl(5).bowl(1);
		
		try {
			bowling.bowl(5);
			fail("IllegalArgumentException should have been thrown.");
		} catch (IllegalArgumentException e) {
			assertEquals("You cannot bowl more in a completed game.", e.getMessage());
		}
	}

	@Test 
	public void shouldPreventRecordingExtraBowlAfterPerfectGame() throws Exception {
		bowling.bowl(10)
			.bowl(10)
			.bowl(10)
			.bowl(10)
			.bowl(10)
			.bowl(10)
			.bowl(10)
			.bowl(10)
			.bowl(10)
			.bowl(10)
			.bowl(10)
			.bowl(10);
		try {
			bowling.bowl(5);
			fail("IllegalArgumentException should have been thrown.");
		} catch (IllegalArgumentException iae) {
			assertEquals("You cannot bowl more in a completed game.", iae.getMessage());
		}	
	}

	@Test
	public void shouldOutputScoreboardFrameByFrame() throws Exception {
		bowling.bowl(1).bowl(2)
			.bowl(3).bowl(4)
			.bowl(5).bowl(4)
			.bowl(3).bowl(2)
			.bowl(1).bowl(0)
			.bowl(1).bowl(2)
			.bowl(3).bowl(4)
			.bowl(5).bowl(4)
			.bowl(3).bowl(2)
			.bowl(1).bowl(0);

			String expectedScoreboard = "[1,2],[3,4],[5,4],[3,2],[1,0],[1,2],[3,4],[5,4],[3,2],[1,0]";
			String actualScoreboard = bowling.printScoreboard();

			assertEquals(expectedScoreboard, actualScoreboard);
	}

}
