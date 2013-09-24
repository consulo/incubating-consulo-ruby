module M1
  class C1
  @a
  @b
  @c
  @a
  @@a
  @@a
  @@b
  @@c

  def f1
    end

    def f2
    end

    alias f2 f1
    alias f3 f2
    alias f4 f3
  end

  def f1
  end

  module M1
    def f1
    end

    class C1
      def f1
      end
    end
  end

  def f1
  end
end

class m2::C1
  def f1
  end
end

def f1
$A = 1
$B = 2
$C
end

A=1
B=1
C=1
