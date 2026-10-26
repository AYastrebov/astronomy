//[astronomy](../../../index.md)/[io.github.cosinekitty.astronomy](../index.md)/[StateVector](index.md)/[StateVector](-state-vector.md)

# StateVector

constructor(x: [Double](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-double/index.html), y: [Double](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-double/index.html), z: [Double](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-double/index.html), vx: [Double](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-double/index.html), vy: [Double](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-double/index.html), vz: [Double](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-double/index.html), t: [Time](../-time/index.md))

constructor(pos: [Vector](../-vector/index.md), vel: [Vector](../-vector/index.md), time: [Time](../-time/index.md))

Combines a position vector and a velocity vector into a single state vector.

#### Parameters

common

| | |
|---|---|
| pos | A position vector. |
| vel | A velocity vector. |
| time | The common time that represents the given position and velocity. |
