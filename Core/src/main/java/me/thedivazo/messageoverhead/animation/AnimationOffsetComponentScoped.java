package me.thedivazo.messageoverhead.animation;

import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import me.thedivazo.messageoverhead.util.Position;

import java.util.Objects;

public class AnimationOffsetComponentScoped implements ComponentScoped<Position> {
    private final int ticks;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final CubicBezier speedCurve;

    private int elapsedTicks;

    public AnimationOffsetComponentScoped(int ticks, double offsetX, double offsetY, double offsetZ) {
        this(ticks, offsetX, offsetY, offsetZ, CubicBezier.linear());
    }

    public AnimationOffsetComponentScoped(
            int ticks,
            double offsetX,
            double offsetY,
            double offsetZ,
            double controlX1,
            double controlY1,
            double controlX2,
            double controlY2
    ) {
        this(ticks, offsetX, offsetY, offsetZ, new CubicBezier(controlX1, controlY1, controlX2, controlY2));
    }

    public AnimationOffsetComponentScoped(
            int ticks,
            double offsetX,
            double offsetY,
            double offsetZ,
            CubicBezier speedCurve
    ) {
        if (ticks <= 0) {
            throw new IllegalArgumentException("ticks must be positive");
        }
        this.ticks = ticks;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speedCurve = Objects.requireNonNull(speedCurve, "speedCurve");
    }

    @Override
    public void onTick(Position context) {
        if (elapsedTicks < ticks) {
            elapsedTicks++;
        }

        double progress = speedCurve.apply((double) elapsedTicks / ticks);
        context.x += offsetX * progress;
        context.y += offsetY * progress;
        context.z += offsetZ * progress;
    }

    public static final class CubicBezier {
        private static final int NEWTON_ITERATIONS = 8;
        private static final int BISECTION_ITERATIONS = 16;
        private static final double EPSILON = 0.0000001;
        private static final double MIN_SLOPE = 0.000001;

        private final double x1;
        private final double y1;
        private final double x2;
        private final double y2;
        private final boolean linear;

        public CubicBezier(double x1, double y1, double x2, double y2) {
            validateFinite(x1, "x1");
            validateFinite(y1, "y1");
            validateFinite(x2, "x2");
            validateFinite(y2, "y2");
            if (x1 < 0.0 || x1 > 1.0 || x2 < 0.0 || x2 > 1.0) {
                throw new IllegalArgumentException("control point x values must be in [0, 1]");
            }
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.linear = Double.compare(x1, y1) == 0 && Double.compare(x2, y2) == 0;
        }

        public static CubicBezier linear() {
            return new CubicBezier(0.0, 0.0, 1.0, 1.0);
        }

        public double apply(double progress) {
            if (progress <= 0.0) {
                return 0.0;
            }
            if (progress >= 1.0) {
                return 1.0;
            }
            if (linear) {
                return progress;
            }
            return sampleY(solveT(progress));
        }

        private double solveT(double progress) {
            double t = progress;
            for (int i = 0; i < NEWTON_ITERATIONS; i++) {
                double difference = sampleX(t) - progress;
                if (Math.abs(difference) < EPSILON) {
                    return t;
                }

                double slope = sampleXDerivative(t);
                if (Math.abs(slope) < MIN_SLOPE) {
                    break;
                }

                double nextT = t - difference / slope;
                if (nextT < 0.0 || nextT > 1.0) {
                    break;
                }
                t = nextT;
            }

            double lower = 0.0;
            double upper = 1.0;
            t = progress;

            for (int i = 0; i < BISECTION_ITERATIONS; i++) {
                double x = sampleX(t);
                if (Math.abs(x - progress) < EPSILON) {
                    break;
                }

                if (x < progress) {
                    lower = t;
                } else {
                    upper = t;
                }
                t = (lower + upper) * 0.5;
            }

            return t;
        }

        private double sampleX(double t) {
            return sample(t, x1, x2);
        }

        private double sampleY(double t) {
            return sample(t, y1, y2);
        }

        private double sampleXDerivative(double t) {
            double inverseT = 1.0 - t;
            return 3.0 * inverseT * inverseT * x1
                    + 6.0 * inverseT * t * (x2 - x1)
                    + 3.0 * t * t * (1.0 - x2);
        }

        private static double sample(double t, double control1, double control2) {
            double inverseT = 1.0 - t;
            return 3.0 * inverseT * inverseT * t * control1
                    + 3.0 * inverseT * t * t * control2
                    + t * t * t;
        }

        private static void validateFinite(double value, String name) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException(name + " must be finite");
            }
        }
    }
}
