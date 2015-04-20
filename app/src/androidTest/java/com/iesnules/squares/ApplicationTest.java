package com.iesnules.squares;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class
        ApplicationTest extends ApplicationTestCase<Application> {
    // For game engine tests
    protected byte[][] mValidGameState;
    protected byte[][] mInvalidGameState;

    public ApplicationTest() {
        super(Application.class);
    }

    protected void setUp() {
        mValidGameState = new byte[][]{{ -1,  1, -1,  1, -1,  0, -1,  1, -1,  1, -1,  1, -1},
                                       {  1,  1,  1,  0,  1,  0,  1,  1,  1,  2,  1,  1,  1},
                                       { -1,  1, -1,  0, -1,  0, -1,  1, -1,  1, -1,  1, -1},
                                       {  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
                                       { -1,  0, -1,  1, -1,  1, -1,  0, -1,  0, -1,  0, -1},
                                       {  0,  0,  1,  2,  1,  2,  1,  0,  1,  0,  0,  0,  0},
                                       { -1,  0, -1,  1, -1,  1, -1,  0, -1,  1, -1,  0, -1}
                                      };

        mInvalidGameState = new byte[][]{{ -1,  1, -1,  1, -1,  0, -1,  1, -1,  1, -1,  1, -1},
                                         {  1,  1,  1,  0,  1,  0,  1,  1,  1,  2,  1,  1,  1},
                                         { -1,  1, -1,  0, -1,  0, -1,  1, -1,  1, -1,  1, -1},
                                         {  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
                                         { -1,  0, -1,  1, -1,  1, -1,  0, -1,  0, -1,  0, -1},
                                         {  0,  0,  1,  2,  1,  2,  1,  0,  1,  0,  0,  1,  0},
                                         { -1,  0, -1,  0, -1,  1, -1,  0, -1,  1, -1,  0, -1}
                                        };
    }

    protected  void tearDown() {
        mValidGameState = null;
        mInvalidGameState = null;
    }

    /*
        Test game engine
     */

    public void testGameEngineNegativeRows() {
        try {
            GameEngine engine = new GameEngine(-1,1);
            fail("Should throw a RuntimeException because of negative number of rows.");
        }
        catch (Exception e) {
            assertNotNull("Exception raised when trying to create a game engine for a game board with a negative number of rows.", e);
        }
    }

    public void testGameEngineNegativeCols() {
        try {
            GameEngine engine = new GameEngine(1,-1);
            fail("Should throw a RuntimeException because of negative number of cols.");
        }
        catch (Exception e) {
            assertNotNull("Exception raised when trying to create a game engine for a game board with a negative number of cols.", e);
        }
    }

    public void testGameEngineValidState() {
        try {
            GameEngine engine = new GameEngine(mValidGameState);
            assertNotNull("Game engine created with a valid state.", engine);
        }
        catch (Exception e) {
            fail("A game engine with a valid state should have been created.");
        }
    }

    public void testGameEngineInvalidState() {
        try {
            GameEngine engine = new GameEngine(mInvalidGameState);
            fail("Should throw a RuntimeException because of invalid game state.");
        }
        catch (Exception e) {
            assertNotNull("Exception raised when trying to create a game engine for an invalid game state.", e);
        }
    }

    public void testGameEngineNewBoardIsEmpty() {
        GameEngine engine = new GameEngine(10,10);
        assertEquals("New game board should have 0 squares captured.", 0, engine.numOfCapturedSquares());
    }

    public void testGameEngineRightNumberOfCaptureSquares() {
        GameEngine engine = new GameEngine(mValidGameState);
        assertEquals("Right number of captured images.", 6, engine.numOfCapturedSquares());
    }

}