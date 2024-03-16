package com.ibis.geometry.common;

data class Quadrilateral(val a: Complex, val b: Complex, val c: Complex, val d: Complex): AbstractPolygon() {
    override val points = listOf(a, b, c, d)
    override fun rebuild(f: (Complex) -> Complex) = Quadrilateral(f(a), f(b), f(c), f(d))
}
