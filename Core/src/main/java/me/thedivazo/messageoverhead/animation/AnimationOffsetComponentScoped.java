package me.thedivazo.messageoverhead.animation;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.component.PositionComponent;
import me.thedivazo.messageoverhead.core.component.scope.ComponentScoped;
import me.thedivazo.messageoverhead.core.component.scope.ScopedFactory;
import me.thedivazo.messageoverhead.util.Position;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AnimationOffsetComponentScoped implements ComponentScoped<Position> {
    public static final String UP_ANIMATION_SCOPED_ID = "up-animation";
    private static final int DEFAULT_TICKS = 15;
    private static final ScopedFactory<Position> DEFAULT_FACTORY =
            ignored -> new AnimationOffsetComponentScoped(DEFAULT_TICKS, 0, 0, 0);

    private final int ticks;
    private final CubicBezier speedCurve;

    private double startOffsetX;
    private double startOffsetY;
    private double startOffsetZ;
    private double targetOffsetX;
    private double targetOffsetY;
    private double targetOffsetZ;
    private double currentOffsetX;
    private double currentOffsetY;
    private double currentOffsetZ;
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
        this.speedCurve = Objects.requireNonNull(speedCurve, "speedCurve");
        setTargetOffset(offsetX, offsetY, offsetZ);
    }

    public void setTargetOffset(double offsetX, double offsetY, double offsetZ) {
        validateFinite(offsetX, "offsetX");
        validateFinite(offsetY, "offsetY");
        validateFinite(offsetZ, "offsetZ");

        startOffsetX = currentOffsetX;
        startOffsetY = currentOffsetY;
        startOffsetZ = currentOffsetZ;
        targetOffsetX = offsetX;
        targetOffsetY = offsetY;
        targetOffsetZ = offsetZ;
        elapsedTicks = 0;
    }

    public void setOffset(double offsetX, double offsetY, double offsetZ) {
        setTargetOffset(offsetX, offsetY, offsetZ);
    }

    public double targetOffsetX() {
        return targetOffsetX;
    }

    public double targetOffsetY() {
        return targetOffsetY;
    }

    public double targetOffsetZ() {
        return targetOffsetZ;
    }

    public static @Nullable AnimationOffsetComponentScoped getOrAttach(ActiveBubble bubble) {
        Objects.requireNonNull(bubble, "bubble");
        return getOrAttach(PositionComponent.getOrAttach(bubble));
    }

    public static @Nullable AnimationOffsetComponentScoped getOrAttach(PositionComponent positionComponent) {
        Objects.requireNonNull(positionComponent, "positionComponent");

        ComponentScoped<Position> scoped = positionComponent.get(UP_ANIMATION_SCOPED_ID);
        if (scoped instanceof AnimationOffsetComponentScoped animationOffset) {
            return animationOffset;
        }

        ComponentScoped<Position> attached = positionComponent.attach(UP_ANIMATION_SCOPED_ID, DEFAULT_FACTORY);
        if (attached instanceof AnimationOffsetComponentScoped animationOffset) {
            return animationOffset;
        }
        return null;
    }

    @Override
    public void onTick(Position context) {
        if (elapsedTicks < ticks) {
            elapsedTicks++;
        }

        double progress = speedCurve.apply((double) elapsedTicks / ticks);
        currentOffsetX = interpolate(startOffsetX, targetOffsetX, progress);
        currentOffsetY = interpolate(startOffsetY, targetOffsetY, progress);
        currentOffsetZ = interpolate(startOffsetZ, targetOffsetZ, progress);
        context.x += currentOffsetX;
        context.y += currentOffsetY;
        context.z += currentOffsetZ;
    }

    private static double interpolate(double start, double target, double progress) {
        return start + (target - start) * progress;
    }

    private static void validateFinite(double value, String name) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(name + " must be finite");
        }
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
