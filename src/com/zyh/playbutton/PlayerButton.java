package com.zyh.playbutton;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class PlayerButton extends View {

	public PlayerButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public PlayerButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public PlayerButton(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs == null) {
			return;
		}
		initAttrs(attrs);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);// ���û��ʵķ�񣬿��Ļ���ʵ��
		paint.setAntiAlias(true);
		paint.setStrokeWidth(stokenWidth);

		oval = new RectF();
	}

	private void initAttrs(AttributeSet attrs) {

	}

	int stokenWidth = 15;
	int stokenColor = Color.GRAY;
	int circleBackground = Color.WHITE;
	int centerX = 100;
	int centerY = 100;
	int radius = 50;
	int buttonWidth = 3;
	int buttonColor = Color.BLUE;

	int pading = 5;
	public final static int STATE_CAN_PLAY = 0;
	public final static int STATE_CAN_PAUSE = 1;

	int currentState = STATE_CAN_PLAY;
	int progressColor = Color.BLUE;
	Paint paint = null;

	static int currentProgress = 0;
	int maxProgress = 10000;
	RectF oval = null;

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();

		centerX = width / 2;
		centerY = height / 2;
		radius = width / 2 - pading;
		// canvas�������ǵ�ǰview�Ĵ�С���������Ͻǣ�0��0��
		oval.set(pading + stokenWidth, pading + stokenWidth, width - pading
				- stokenWidth, height - pading - stokenWidth);

		// ��Բ�ı߿�
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(stokenColor);
		paint.setStrokeWidth(stokenWidth);
		canvas.drawArc(oval, -90, 360.0f, false, paint);
		// ��������
		paint.setColor(progressColor);
		canvas.drawArc(oval, -90, currentProgress * 360.0f / maxProgress,
				false, paint);

		// ��Բ�ı���
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(circleBackground);
		canvas.drawCircle(centerX, centerY, radius - stokenWidth, paint);

		// ���м䰴ť
		paint.setColor(buttonColor);
		paint.setStrokeWidth(buttonWidth);
		float[] pts = null;
		if (currentState == STATE_CAN_PLAY) {
			pts = new float[] { centerX - width / 10.3923f,
					centerY - width / 6f, centerX - width / 10.3923f,
					centerY + width / 6f, centerX - width / 10.3923f,
					centerY + width / 6f, centerX + width / 6f, centerY,
					centerX + width / 6f, centerY, centerX - width / 10.3923f,
					centerY - width / 6f };

		} else {
			pts = new float[] { centerX - width / 10f, centerY - width / 6f,
					centerX - width / 10f, centerY + width / 6f,
					centerX + width / 10f, centerY - width / 6f,
					centerX + width / 10f, centerY + width / 6f, };
		}
		canvas.drawLines(pts, paint);
		// super.onDraw(canvas);
	}

	public void setProgress(int progress) {
		synchronized (this) {
			currentProgress = progress;
			invalidate();
		}
	}

	public void setMax(int max) {
		synchronized (this) {
			maxProgress = max;
		}
	}

	public void setState(int state) {
		synchronized (this) {
			if (state == STATE_CAN_PLAY) {
				currentState = STATE_CAN_PLAY;
			} else {
				currentState = STATE_CAN_PAUSE;
			}
			invalidate();
		}
	}

	public void changeState() {
		synchronized (this) {
			if (currentState == STATE_CAN_PLAY) {
				currentState = STATE_CAN_PAUSE;
			} else {
				currentState = STATE_CAN_PLAY;
			}
			Log.d("", "�л�״̬:" + currentState);
			invalidate();
		}
	}

	boolean isPlayComplete = true;

	public void play(final Activity activity) {
		synchronized (this) {
			Log.d("", "��ǰ����״̬=" + currentState + "");
			if (currentState == STATE_CAN_PLAY) {
				changeState();
				final ValueAnimator va = ValueAnimator.ofInt(currentProgress,
						maxProgress).setDuration(maxProgress-currentProgress);
				va.setInterpolator(new LinearInterpolator());
				va.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						if (currentState == STATE_CAN_PLAY) {
							isPlayComplete = false;
							va.cancel();// ��cancel���ִ��onAnimationCancel Ȼ��
										// onAnimationEnd
						}

						int value = (int) animation.getAnimatedValue();
						Log.d("", "value=" + value + "");
						if (value >= maxProgress) {
							isPlayComplete = true;
						}
						setProgress(value);
					}
				});
				va.addListener(new ProgressAnimListener());
				va.start();
			} else {
				changeState();
			}
		}
	}

	class ProgressAnimListener implements AnimatorListener {
		@Override
		public void onAnimationStart(Animator animation) {
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			Log.d("", "onAnimationEnd------->>isPlayComplete:" + isPlayComplete);
			if (isPlayComplete) {
				setProgress(0);
				changeState();
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			Log.d("", "onAnimationCancel");
		}
	}

}
