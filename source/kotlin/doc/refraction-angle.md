//[astronomy](../../index.md)/[io.github.cosinekitty.astronomy](index.md)/[refractionAngle](refraction-angle.md)

# refractionAngle

fun [refractionAngle](refraction-angle.md)(refraction: [Refraction](-refraction/index.md), altitude: [Double](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-double/index.html)): [Double](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-double/index.html)

Calculates the amount of &quot;lift&quot; to an altitude angle caused by atmospheric refraction.

Given an altitude angle and a refraction option, calculates the amount of &quot;lift&quot; caused by atmospheric refraction. This is the number of degrees higher in the sky an object appears due to the lensing of the Earth's atmosphere. This function works best near sea level. To correct for higher elevations, call [atmosphere](atmosphere.md) for that elevation and multiply the refraction angle by the resulting relative density.

#### Parameters

common

| | |
|---|---|
| refraction | The option selecting which refraction correction to use. If `Refraction.Normal`, uses a well-behaved refraction model that works well for all valid values (-90 to +90) of `altitude`. If `Refraction.JplHor`, this function returns a compatible value with the JPL Horizons tool. If any other value, including `Refraction.None`, this function returns 0. |
| altitude | An altitude angle in a horizontal coordinate system. Must be a value between -90 and +90. |
