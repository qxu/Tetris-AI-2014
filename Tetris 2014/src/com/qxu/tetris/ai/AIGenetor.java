package com.qxu.tetris.ai;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.qxu.tetris.ai.scores.FinalRater;
import com.qxu.tetris.eval.Eval;

public class AIGenetor {
	public static void main(String[] args) {
		AIGenetor gen = new AIGenetor(10, 10);
		while (true) {
			gen.evolve();
			System.out.println(gen.generation + ": "
					+ gen.population[0].fitness);
			System.out.println(Arrays.toString(gen.getBestCoeff())
					.replace('[', '{').replace(']', '}'));
			System.out.println();
		}
	}

	private int breedSize = 10;

	private Random rand = new Random();
	private int generation = 0;

	private Eval eval;
	private ScorePair[] population;

	public AIGenetor(int gridHeight, int gridWidth) {
		this.eval = new Eval(gridHeight, gridWidth);
		population = new ScorePair[breedSize * (breedSize + 1) / 2 + breedSize];
		for (int i = 0; i < population.length; i++) {
			double[] c = new double[FinalRater.RATERS.length];
			population[i] = new ScorePair(c, 0);
		}
	}

	public int getGeneration() {
		return this.generation;
	}

	public double[] getBestCoeff() {
		return population[0].c;
	}

	public void evolve() {
		ScorePair[] fittest = new ScorePair[breedSize];
		System.arraycopy(population, 0, fittest, 0, breedSize);

		int i = 0;
		while (i < breedSize) {
			double[] child = population[i].c;
			double fitness = getFitness(child);
			population[i++] = new ScorePair(child, fitness);
		}
		for (int p = 0; p < fittest.length; p++) {
			double[] child = fittest[p].c.clone();
			mutate(child);
			double fitness = getFitness(child);
			population[i++] = new ScorePair(child, fitness);
		}
		for (int p1 = 0; p1 < fittest.length; p1++) {
			for (int p2 = p1 + 1; p2 < fittest.length; p2++) {
				double[] child = breed(fittest[p1].c, fittest[p2].c);
				double fitness = getFitness(child);
				population[i++] = new ScorePair(child, fitness);
			}
		}
		Arrays.sort(population, Collections.reverseOrder());

		generation++;
	}

	private double[] breed(double[] p1, double[] p2) {
		double[] child = new double[FinalRater.RATERS.length];
		for (int i = 0; i < child.length; i++) {
			child[i] = p1[i] * 0.5 + p2[i] * 0.5;
		}
		mutate(child);
		return child;
	}

	private void mutate(double[] c) {
		for (int i = 0; i < c.length; i++) {
			c[i] += (double) (rand.nextInt(10000) - 5000) / 1000.0;
		}
	}

	private double getFitness(double[] c) {
		RaterAI ai = new RaterAI(new FinalRater(c));
		return eval.evalN(ai, 1, 4);
	}

	private static class ScorePair implements Comparable<ScorePair> {
		double[] c;
		double fitness;

		public ScorePair(double[] c, double fitness) {
			this.c = c;
			this.fitness = fitness;
		}

		@Override
		public int compareTo(ScorePair other) {
			return Double.compare(this.fitness, other.fitness);
		}

		@Override
		public String toString() {
			return "" + fitness;
		}
	}
}
