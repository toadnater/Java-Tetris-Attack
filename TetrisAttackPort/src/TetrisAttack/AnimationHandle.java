package TetrisAttack;

import java.util.Vector;
import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class AnimationHandle {
	
	private final String instructionFile = "src/TetrisAttack/animationInstructions";
	private Vector<AnimationInstruction> instructions;
	public String currentState;
	private int currentInstruction;
	public int currentFrame;
	
	public AnimationHandle() {
		instructions = new Vector<AnimationInstruction>();
		currentState = "STOPPED";
		currentFrame = 0;
		currentInstruction = 0;	
	}
	
	public void printAllInstructions() {
		for (AnimationInstruction i : instructions) {
			System.out.println("frame: " + i.frameNumber +
								"image: " + i.imageNumber +
								"x: " + i.x + "y: " + i.y +
								"width: " + i.width +
								"height: " + i.height
								);
		}
	}
	
	public int numberOfInstructions() {
		return instructions.size();
	}
	
	public boolean updateInstruction(int instructNumber, AnimationInstruction newInstruction) {
		if (instructNumber < instructions.size() && instructNumber >= 0) {
			instructions.set(instructNumber, newInstruction);
			return true;
		}
		return false;
	}
	
	public boolean removeInstruction(int instructNumber) {
		if (instructNumber < instructions.size() && instructNumber >= 0) {
			instructions.remove(instructNumber);
			return true;
		}
		return false;
	}
	
	public boolean CreateSlide(int keyFrameA, int keyFrameB) {
		try {
			AnimationInstruction A = instructions.get(keyFrameA);
			AnimationInstruction B = instructions.get(keyFrameB);
			
			int frameNum = Math.abs(B.frameNumber - A.frameNumber);
			double deltaY = (double)(B.y - A.y) / (double)frameNum;
			double deltaX = (double)(B.x - A.x) / (double)frameNum;
			
			int sameImageIndex = A.imageNumber;		// Always uses the first image number
			int sameWidth = A.width;
			int sameHeight = A.height;
	
			for (int i = A.frameNumber + 1; i < B.frameNumber; i++) {
				AnimationInstruction newInstruction = new AnimationInstruction();
				newInstruction.frameNumber = i;
				newInstruction.imageNumber = sameImageIndex;
				newInstruction.x = A.x + (int)(deltaX * (double)(i - A.frameNumber));
				newInstruction.y = A.y + (int)(deltaY * (double)(i - A.frameNumber));
				newInstruction.width = sameWidth;
				newInstruction.height = sameHeight;
				// Alpha is left as default 0.0
				
				instructions.add(keyFrameA + (i - A.frameNumber), newInstruction);
			}
		} catch (Exception e) {
			// Maybe there was an error adding the instruction? Or getting the instruction
			// with the index given?
			System.out.println("AnimationHandle.CreateSlide(): Error: " + e);
			return false;
		}
		return true;
	}
	
	public void start() {
		currentState = "PLAYING";
	}
	
	public AnimationInstruction resetToFirstFrame() {
		currentFrame = 0;
		return (instructions.firstElement());
	}
	
	// To play an animation backwards
	public void reverseInstructions() {
		Vector<AnimationInstruction> reverseVector = new Vector<AnimationInstruction>();
		int size = instructions.size() - 1; // Highest index for instructions
		for (int i = size; i >= 0; i--) {
			reverseVector.add(instructions.get(i));
			reverseVector.get(size - i).frameNumber = instructions.get(size - i).frameNumber;
		}
		instructions = reverseVector;
	}
		
	public AnimationInstruction getInstruction(int index) {
		if (index >= 0 && index < instructions.size()) {
			return instructions.get(index);
		}
		return null;
	}
	
	// If we only have instructions for frame 0 and 12, then if we ask for the instruction
	// at frames 1-11, we need to continue returning the instructions for frame 1, because
	// nothing is supposed to happen until frame 12.
	public AnimationInstruction nextInstruction() {
		//System.out.println("AnimationHandle.nextInstruction(): currentFrame: " + currentFrame);
		//System.out.println("AnimationHandle.nextInstruction(): currentInstruction: " + currentInstruction);
		//System.out.println("AnimationHandle.nextInstruction(): size: " + instructions.size());
		if (currentInstruction + 1 < instructions.size()) {
			currentFrame++;
			if (instructions.get(currentInstruction + 1).frameNumber >= currentFrame) {
				return instructions.get(currentInstruction);
			}
			return instructions.get(++currentInstruction);
		} else {
			//System.out.println("AnimationHandle.nextInstruction(): Next frame does not exist / End of animation");
			//System.out.println("      -- Stopping animation. ");
			this.end();
		}
		return null;
	}
	
	private void parseInstructions(String instruction) {
		AnimationInstruction tempInstruction = new AnimationInstruction();	
		String instructTokens[] = instruction.trim().split("\\s+");	// Separates by whitespace (not just " ")s.
		try {
			tempInstruction.frameNumber = Integer.parseInt(instructTokens[0]);
			tempInstruction.imageNumber = Integer.parseInt(instructTokens[1]);
			tempInstruction.x = Integer.parseInt(instructTokens[2]);
			tempInstruction.y = Integer.parseInt(instructTokens[3]);
			if ((tempInstruction.width = (int)((double)Integer.parseInt(instructTokens[4]) * Main.globalScale)) <= 0) {
				tempInstruction.width = 1;
			}
			if ((tempInstruction.height = (int)((double)Integer.parseInt(instructTokens[5]) * Main.globalScale)) <= 0) {
				tempInstruction.height = 1;
			}
			tempInstruction.alpha = Float.parseFloat(instructTokens[6]);
		} catch (Exception e) {
			System.out.println("AnimationHandle.parseInstructions(): Error: " + e.toString());
		}
		instructions.addElement(tempInstruction);
	}
	
	public boolean loadInstructions(String graphicName) {
		// After deciding how graphics will be formatted, this is how we will load them
		// for each "movie clip".
		try {
			File animationFile = new File(instructionFile);
			Scanner animationScanner = new Scanner(new FileReader(animationFile));
			animationScanner.useDelimiter("\n");
			while (animationScanner.hasNext()) {
				String readLine = animationScanner.next();
				if (readLine.length() > graphicName.length()) {
					if (readLine.substring(0, graphicName.length() + 1).equals(graphicName + ":")) {
						try {
							while (!(readLine = animationScanner.next()).trim().isEmpty()) {
								parseInstructions(readLine);
							}
						} catch (Exception e) {
							System.out.println("AnimationHandle.loadInstructions(): next() returns null value - end of instruction set");
						}
					}
				}
			}
			return true;
		} catch (Exception e) {
			System.out.println("AnimationHandle.loadInstructions(): Error: " + e.toString());
		}
		return false;
	}
	
	public void end() {
		currentState = "STOPPED";
		currentInstruction = 0;
		currentFrame = 0;
	}
	
	public void stop() {
		currentState = "STOPPED";
	}
	
	public boolean isPlaying() {
		return (currentState.equals("PLAYING"));
	}
	
	public boolean hasInstructions() {
		return (instructions.isEmpty());
	}
}

final class AnimationInstruction {
	public int frameNumber;
	public int imageNumber;
	public int x;
	public int y;
	public int width;
	public int height;
	public float alpha;
	
	public AnimationInstruction() {
		frameNumber = -1;
		imageNumber = -1;
		x = -1;
		y = -1;
		width = 1;
		height = 1;
		alpha = 1.0f;
	}
	
	public AnimationInstruction(int f, int i) {
		this();
		frameNumber = f;
		imageNumber = i;
	}
	
	public AnimationInstruction(int f, int i, int xx, int yy) {
		this(f, i);
		x = xx;
		y = yy;
	}
	
	public AnimationInstruction(int f, int i, int xx, int yy, float a) {
		this(f, i, xx, yy);
		alpha = a;
	}
}