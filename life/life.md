# Conway's Game of Life

A Python implementation of Conway's Game of Life, a cellular automaton that simulates the evolution of a population of cells based on simple rules.

## Description

This simulation creates a grid of cells that can be either alive or dead. Each generation, the cells evolve according to the rules of Conway's Game of Life. The board starts with a random initial configuration and evolves for a specified number of generations.

## Rules

The Game of Life follows these simple rules:

1. **Survival**: A live cell with 2 or 3 live neighbors survives to the next generation
2. **Birth**: A dead cell with exactly 3 live neighbors becomes alive in the next generation
3. **Death**: All other cells die or remain dead

## Features

- Random initial board generation
- Toroidal (wrapping) boundaries - the board wraps around at the edges
- Visual output using `█` for alive cells and spaces for dead cells
- Generation counter displayed with each board state

## Running the Game

### Prerequisites

- Python 3.x

### Usage

Run the simulation with the following command:

```bash
python life.py <board_size> <num_generations>
```

### Arguments

- `board_size`: The size of the square board (must be a positive integer)
- `num_generations`: The number of generations to simulate (must be a non-negative integer)

### Examples

Run a 20x20 board for 10 generations:
```bash
python life.py 20 10
```

Run a 30x30 board for 50 generations:
```bash
python life.py 30 50
```

Run a 10x10 board for 5 generations:
```bash
python life.py 10 5
```

## Output

The program prints the board state after each generation, showing:
- Generation number
- Visual representation of the board (█ = alive, space = dead)

Each generation is displayed sequentially, allowing you to observe the evolution of the cellular automaton.

