package com.upl.mmorpg.lib.collision;

public class CollideBox extends CollideShape
{
	public CollideBox(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public boolean inside(double x, double y)
	{
		if(x < this.x) return false;
		if(x > this.x + width) return false;
		if(y < this.y) return false;
		if(y > this.y + height) return false;
		
		return true;
	}
	
	@Override
	public boolean collidesBox(CollideBox shape) 
	{
		double x1 = shape.x;
		double x2 = x1 + shape.width;
		double y1 = shape.y;
		double y2 = y1 + shape.height;
		
		if(inside(x1, y1)) return true;
		if(inside(x1, y2)) return true;
		if(inside(x2, y1)) return true;
		return inside(x2, y2);
	}

	@Override
	public boolean collidesCircle(CollideCircle shape) 
	{
		double x2 = x + width;
		double y2 = y + height;
		
		double cx = shape.getCenterX();
		double cy = shape.getCenterY();
		double r = shape.getRadius();
		
		if(inside(cx, cy)) 
			return true;
		if(Math.abs(x - cx) < r && (cy > y && cy < y2))
			return true;
		if(Math.abs(cx - x2) < r && (cy > y && cy < y2))
			return true;
		if(Math.abs(y - cy) < r && (cy > x && cy < x2))
			return true;
		if(Math.abs(cy - y) < r && (cy > x && cy < x2))
			return true;
		
		if(shape.inside(x, y)) return true;
		if(shape.inside(x, y2)) return true;
		if(shape.inside(x2, y)) return true;
		return shape.inside(x2, x2);
	}
	
	public boolean boundsBox(CollideBox box)
	{
		double otherX1 = box.getX();
		double otherY1 = box.getY();
		double otherX2 = otherX1 + box.getWidth();
		double otherY2 = otherY1 + box.getHeight();
		
		if(otherX1 < x) return false;
		if(otherY1 < y) return false;
		if(otherX2 > x + width) return false;
		if(otherY2 > y + height) return false;
		
		return true;
	}
	
	public boolean boundsCircle(CollideCircle circle)
	{
		double cx = circle.getCenterX();
		double cy = circle.getCenterY();
		double r = circle.getRadius();
		
		double topY = cy - r;
		double bottomY = cy + r;
		double rightX = cx + r;
		double leftX = cx - r;
		
		if(rightX > x + width) return false;
		if(topY < y) return false;
		if(leftX < x) return false;
		if(bottomY < y + height)return false;
		
		return true;
	}

	public double getX() {return x;}
	public void setX(double x) {this.x = x;}
	public double getY() {return y;}
	public void setY(double y) {this.y = y;}
	public double getWidth() {return width;}
	public void setWidth(double width) {this.width = width;}
	public double getHeight() {return height;}
	public void setHeight(double height) {this.height = height;}
	
	private double x;
	private double y;
	private double width;
	private double height;
}
