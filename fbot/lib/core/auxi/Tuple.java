package fbot.lib.core.auxi;

public class Tuple<E, F>
{
  public final E x;
  public final F y;
  
  public Tuple(E x, F y)
  {
    this.x = x;
    this.y = y;
  }
  
  public String conc(String format)
  {
    return this.x.toString() + this.y.toString();
  }
  
  public boolean equals(Tuple<E, F> other)
  {
    return (other != null) && ((other instanceof Tuple)) && (this.y.equals(other.y)) && (this.x.equals(other.x));
  }
  
  public String toString()
  {
    return String.format("(%s, %s)", new Object[] { this.x.toString(), this.y.toString() });
  }
}
