import random
import sys


def create_random_board(size):
    """Create a random initial board."""
    return [[random.choice([0, 1]) for _ in range(size)] for _ in range(size)]


def parse_board_string(board_string, size):
    """Parse a string of zeros and ones into a 2D board."""
    if len(board_string) != size * size:
        print(f"Error: board string must have exactly {size * size} characters (size={size}), got {len(board_string)}")
        sys.exit(1)
    
    board = []
    for i in range(size):
        row = []
        for j in range(size):
            char = board_string[i * size + j]
            if char == '1':
                row.append(1)
            elif char == '0':
                row.append(0)
            else:
                print(f"Error: board string must contain only '0' and '1', found '{char}'")
                sys.exit(1)
        board.append(row)
    
    return board


def is_board_empty(board):
    """Check if the board is fully empty (all zeros)."""
    for row in board:
        for cell in row:
            if cell == 1:
                return False
    return True


def board_to_tuple(board):
    """Convert a board to a tuple of tuples for hashing."""
    return tuple(tuple(row) for row in board)


def print_board(board, generation):
    """Print the board with generation number."""
    print(f"\nGeneration {generation}:")
    for row in board:
        print(''.join(['1' if cell else '0' for cell in row]))


def count_neighbors(board, row, col):
    """Count the number of live neighbors around a cell."""
    size = len(board)
    count = 0
    for i in range(-1, 2):
        for j in range(-1, 2):
            if i == 0 and j == 0:
                continue
            neighbor_row = row + i
            neighbor_col = col + j
            # Only count neighbors that are within board bounds
            if 0 <= neighbor_row < size and 0 <= neighbor_col < size:
                count += board[neighbor_row][neighbor_col]
    return count


def next_generation(board, size):
    """Calculate the next generation based on Game of Life rules."""
    new_board = [[0 for _ in range(size)] for _ in range(size)]
    
    for row in range(size):
        for col in range(size):
            neighbors = count_neighbors(board, row, col)
            
            # Game of Life rules:
            # 1. Any live cell with 2 or 3 neighbors survives
            # 2. Any dead cell with exactly 3 neighbors becomes alive
            # 3. All other cells die or stay dead
            if board[row][col] == 1:
                if neighbors == 2 or neighbors == 3:
                    new_board[row][col] = 1
            else:
                if neighbors == 3:
                    new_board[row][col] = 1
    
    return new_board


def main():
    """Main function to run the Game of Life simulation."""
    if len(sys.argv) < 3 or len(sys.argv) > 4:
        print("Usage: python life.py <board_size> <num_generations> [board_string]")
        print("  board_string: optional string of zeros and ones representing the initial board")
        sys.exit(1)
    
    try:
        size = int(sys.argv[1])
        num_generations = int(sys.argv[2])
    except ValueError:
        print("Error: board_size and num_generations must be integers")
        sys.exit(1)
    
    if size <= 0:
        print("Error: board_size must be positive")
        sys.exit(1)
    
    if num_generations < 0:
        print("Error: num_generations must be non-negative")
        sys.exit(1)
    
    # Create initial board from string if provided, otherwise random
    if len(sys.argv) == 4:
        board = parse_board_string(sys.argv[3], size)
    else:
        board = create_random_board(size)
    
    # Print initial board (generation 0)
    print_board(board, 0)
    
    # Check if initial board is empty
    if is_board_empty(board):
        print("\nSimulation stopped: board is empty (all zeros)")
        return
    
    # Track seen board states to detect cycles
    seen_states = {board_to_tuple(board)}
    
    # Run simulation for specified number of generations
    for generation in range(1, num_generations + 1):
        board = next_generation(board, size)
        print_board(board, generation)
        
        # Stop if board becomes empty
        if is_board_empty(board):
            print(f"\nSimulation stopped at generation {generation}: board is empty (all zeros)")
            break
        
        # Check if we've seen this board state before (cycle detection)
        board_tuple = board_to_tuple(board)
        if board_tuple in seen_states:
            print(f"\nSimulation stopped at generation {generation}: board state repeats (cycle detected)")
            break
        
        # Add current board state to seen states
        seen_states.add(board_tuple)


if __name__ == "__main__":
    main()

