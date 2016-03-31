package com.ahci.meme_recommender.auxiliary;

import android.graphics.PointF;

public class AnalyticGeometry {
    public static final PointF getGeometricVector(final PointF a, final PointF b) {
        return new PointF(b.x - a.x, b.y - a.y);
    }

    public static final double getGeometricVectorNorm(final PointF a) {
        return Math.sqrt(a.x * a.x + a.y * a.y);
    }

    public static final double getGeometricVectorDotProduct(final PointF a, final PointF b) {
        return a.x * b.x + a.y * b.y;
    }

    public static final double getGeometricVectorCrossProduct(final PointF a, final PointF b) {
        return a.x * b.y + a.y * b.y;
    }

    public static final double getAngleBetweenVectorsInDegrees(final PointF a, final PointF b) {
        return (Math.acos(getGeometricVectorDotProduct(a, b) / getGeometricVectorNorm(a) * getGeometricVectorNorm(b)) * 180d / Math.PI);
    }

    public static final PointF getVectorIntersection(final PointF p, final PointF q, final PointF r, final PointF s) {
        PointF x = new PointF(r.x - p.x, r.y - p.y);
        PointF d1 = new PointF(q.x - p.x, q.y - p.y);
        PointF d2 = new PointF(s.x - r.x, s.y - r.y);

        double t = getGeometricVectorCrossProduct(x, d2) / getGeometricVectorCrossProduct(d1, d2);

        return new PointF((float) (t * (p.x + d1.x)), (float) (t * (p.y + d1.y)));
    }
}
