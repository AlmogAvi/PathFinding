Grid Pathfinding Visualizer (Java/Swing)

An educational, interactive visualizer for classic grid pathfinding algorithms. It lets you compare unweighted vs. weighted search, see how heuristics steer A*, and understand trade-offs in speed vs. optimality—all on a live, animated grid.

Key Features

Algorithms

BFS, DFS

Dijkstra

A* with selectable heuristics (Manhattan / Octile)

Weighted A* (tunable w)

Greedy Best-First with optional step-cost penalty (λ)

Bidirectional BFS

Weighted grids & terrain

Per-cell cost (e.g., cheap “highway”, expensive “mud”), walls, and open cells

Optional diagonal moves with √2 cost factor

Avoid corner cut toggle to prevent slipping through tight corners

Live visualization

Shows queued and visited cells, and the final path

Per-cell cost labels

Auto-fit, responsive grid that fills the window

On finish: displays steps and total path cost

Controls

Algorithm selector

Speed slider

Toggles: Diagonals, Avoid corner cut

A* weight slider (w, 1.00–3.00)

Greedy penalty slider (λ, 0.00–3.00)

Random maze, Rebuild graph, Reset, Run/Step

How It Works

The grid is turned into a weighted, undirected graph; edge weight = cost to enter the target cell (× √2 for diagonals if enabled).

Algorithms are run via a common Stepper interface that emits StepEvents (queue/visit/done), which the UI consumes to animate progress.

Heuristics:

Manhattan for 4-neighbors

Octile for 8-neighbors (diagonals with √2)

A* can be weighted (f = g + w·h) for faster, less optimal searches.

Greedy Best-First can optionally include a per-step cost penalty (priority = h + λ·stepCost) to avoid expensive terrain while remaining non-optimal.

Project Structure
grid/        GridMap (walls/costs/flags), GridGraphBuilder, RC, Heuristics
graphs/      Graph abstractions, WeightedGraph, steppers’ dependencies, Heuristic
viz/         Swing UI: App (main), GridPanel (rendering), StepEvent, Stepper +
             AStarStepper, DijkstraStepper, BfsStepper, DfsStepper,
             GreedyStepper, BidirectionalBfsStepper

Getting Started

Requirements: Java 17+ (or compatible), IntelliJ IDEA (recommended).

Run: Open the project in IntelliJ and run viz.App.main().

Use the top bar to pick an algorithm, tweak speed/heuristics, toggle diagonals/corner-cut, generate a random maze, and step/run the search.

Demo Scenarios

Small contrast scenario: a cheap “highway” vs. expensive “mud” band that forces different choices across BFS/Dijkstra/A*/Greedy.

Random maze: quickly stress-tests algorithms with mixed walls and varied costs.

Extending

Add new algorithms by implementing Stepper.

Add heuristics in grid/Heuristics.

Drop in new terrain distributions or dynamic blockers (e.g., re-planning) as future enhancements.
