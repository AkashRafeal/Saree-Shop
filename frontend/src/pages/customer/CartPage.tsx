import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Trash2, ShoppingBag, ArrowRight, Tag, ShieldCheck } from 'lucide-react';
import { Cart } from '@/types';
import { useAuthStore } from '@/store/authStore';
import api from '@/services/api';

export const CartPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();

  const [cart, setCart] = useState<Cart | null>(null);
  const [couponCode, setCouponCode] = useState('');
  const [appliedCoupon, setAppliedCoupon] = useState<string | null>(null);
  const [discountAmount, setDiscountAmount] = useState(0);
  const [couponMsg, setCouponMsg] = useState<{ text: string; error: boolean } | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    fetchCart();
  }, [isAuthenticated]);

  const fetchCart = () => {
    setLoading(true);
    api.get('/cart')
      .then((res) => setCart(res.data?.data))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  };

  const handleUpdateQuantity = async (itemId: number, newQty: number) => {
    try {
      const res = await api.put(`/cart/items/${itemId}?quantity=${newQty}`);
      setCart(res.data?.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleRemoveItem = async (itemId: number) => {
    try {
      const res = await api.delete(`/cart/items/${itemId}`);
      setCart(res.data?.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleApplyCoupon = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!couponCode.trim() || !cart) return;

    try {
      const res = await api.post('/coupons/validate', {
        code: couponCode.trim(),
        orderAmount: cart.subtotal,
      });
      const data = res.data?.data;
      if (data?.valid) {
        setAppliedCoupon(data.code);
        setDiscountAmount(data.calculatedDiscount);
        setCouponMsg({ text: `Success! ${data.code} applied: ₹${data.calculatedDiscount} off`, error: false });
      }
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Invalid coupon code';
      setCouponMsg({ text: msg, error: true });
    }
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 animate-pulse space-y-6">
        <div className="h-8 bg-stone-200 rounded w-48" />
        <div className="h-64 bg-stone-200 rounded-xl" />
      </div>
    );
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-20 text-center space-y-6">
        <div className="w-20 h-20 mx-auto rounded-full bg-stone-100 flex items-center justify-center text-stone-400">
          <ShoppingBag className="w-10 h-10" />
        </div>
        <h2 className="font-serif text-3xl font-bold text-stone-900">Your Shopping Bag Is Empty</h2>
        <p className="text-xs text-stone-500 max-w-sm mx-auto">
          Explore our handloom creations and drape yourself in timeless royal silks.
        </p>
        <div>
          <Link
            to="/shop"
            className="inline-flex items-center space-x-2 bg-[#0A5C44] hover:bg-[#064E3B] text-white font-bold text-xs py-3.5 px-8 rounded-full uppercase tracking-wider transition shadow-lg shadow-[#0A5C44]/20 cursor-pointer"
          >
            <span>Explore Sarees</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>
      </div>
    );
  }

  const finalGrandTotal = Math.max(0, cart.subtotal - discountAmount + cart.shippingCharge);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      <div>
        <span className="text-xs font-bold uppercase tracking-widest text-[#D4AF37]">
          Review Selection
        </span>
        <h1 className="font-serif text-3xl font-bold text-stone-900 mt-1">
          Shopping Bag ({cart.totalItems} Items)
        </h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
        {/* Left: Cart Items List */}
        <div className="lg:col-span-2 space-y-4">
          <div className="divide-y divide-stone-200 bg-white rounded-2xl border border-stone-200/80 shadow-sm overflow-hidden">
            {cart.items.map((item) => (
              <div key={item.id} className="p-4 sm:p-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                <div className="flex items-center space-x-4">
                  <img
                    src={item.imageUrl}
                    alt={item.productName}
                    className="w-20 h-24 object-cover rounded-xl bg-stone-100 border border-stone-200 shrink-0"
                  />
                  <div>
                    <Link
                      to={`/product/${item.productId}`}
                      className="font-serif text-sm font-semibold text-stone-900 hover:text-[#0A5C44] transition line-clamp-1"
                    >
                      {item.productName}
                    </Link>
                    <p className="text-[11px] text-stone-400 mt-0.5">SKU: {item.productSku}</p>
                    <p className="text-xs font-bold text-stone-800 mt-1">
                      ₹{item.sellingPrice.toLocaleString('en-IN')}
                    </p>
                  </div>
                </div>

                <div className="flex items-center justify-between w-full sm:w-auto space-x-6">
                  {/* Quantity Controls */}
                  <div className="flex items-center border border-stone-300 rounded-full overflow-hidden bg-white px-1">
                    <button
                      onClick={() => handleUpdateQuantity(item.id, item.quantity - 1)}
                      className="w-7 h-7 rounded-full flex items-center justify-center text-stone-600 hover:text-[#0A5C44] hover:bg-emerald-50 text-xs font-bold transition cursor-pointer"
                    >
                      -
                    </button>
                    <span className="px-2.5 py-1 text-xs font-bold text-stone-800">{item.quantity}</span>
                    <button
                      onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                      className="w-7 h-7 rounded-full flex items-center justify-center text-stone-600 hover:text-[#0A5C44] hover:bg-emerald-50 text-xs font-bold transition cursor-pointer"
                    >
                      +
                    </button>
                  </div>

                  {/* Subtotal */}
                  <span className="font-bold text-sm text-stone-900 sm:w-24 text-right">
                    ₹{item.subtotal.toLocaleString('en-IN')}
                  </span>

                  {/* Remove Item */}
                  <button
                    onClick={() => handleRemoveItem(item.id)}
                    className="text-stone-400 hover:text-red-600 transition p-1 cursor-pointer"
                    aria-label="Remove item"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="flex justify-between items-center text-xs">
            <Link to="/shop" className="text-[#0A5C44] font-semibold hover:underline">
              ← Continue Exploring Sarees
            </Link>
          </div>
        </div>

        {/* Right: Summary Card & Coupon */}
        <div className="space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-stone-200/80 shadow-sm space-y-5">
            <h3 className="font-serif text-lg font-bold text-stone-900 border-b border-stone-100 pb-3">
              Order Summary
            </h3>

            {/* Coupon Code Input */}
            <form onSubmit={handleApplyCoupon} className="space-y-2">
              <label className="text-[11px] font-bold uppercase tracking-wider text-stone-500 block">
                Promotional Coupon
              </label>
              <div className="relative flex items-center">
                <input
                  type="text"
                  value={couponCode}
                  onChange={(e) => setCouponCode(e.target.value.toUpperCase())}
                  placeholder="e.g. WELCOME10, FESTIVE20"
                  className="w-full text-xs pl-4 pr-20 py-2.5 rounded-full border border-stone-300 focus:outline-none focus:border-[#0A5C44] uppercase font-semibold transition shadow-inner"
                />
                <button
                  type="submit"
                  className="absolute right-1 bg-[#0A5C44] hover:bg-[#064E3B] text-white font-bold text-xs px-4 py-1.5 rounded-full transition shadow-sm cursor-pointer"
                >
                  Apply
                </button>
              </div>
              {couponMsg && (
                <p className={`text-[11px] font-medium ${couponMsg.error ? 'text-red-600' : 'text-emerald-700'}`}>
                  {couponMsg.text}
                </p>
              )}
            </form>

            {/* Price Calculations */}
            <div className="space-y-3 text-xs border-t border-stone-100 pt-4">
              <div className="flex justify-between text-stone-600">
                <span>Subtotal</span>
                <span>₹{cart.subtotal.toLocaleString('en-IN')}</span>
              </div>

              {discountAmount > 0 && (
                <div className="flex justify-between text-emerald-700 font-semibold">
                  <span className="flex items-center gap-1">
                    <Tag className="w-3.5 h-3.5" />
                    Coupon Discount ({appliedCoupon})
                  </span>
                  <span>-₹{discountAmount.toLocaleString('en-IN')}</span>
                </div>
              )}

              <div className="flex justify-between text-stone-600">
                <span>Insured Express Shipping</span>
                <span>{cart.shippingCharge === 0 ? <span className="text-emerald-700 font-semibold">FREE</span> : `₹${cart.shippingCharge}`}</span>
              </div>

              <div className="flex justify-between text-base font-bold text-stone-900 border-t border-stone-200 pt-3">
                <span>Grand Total</span>
                <span>₹{finalGrandTotal.toLocaleString('en-IN')}</span>
              </div>
            </div>

            <button
              onClick={() => navigate('/checkout', { state: { appliedCoupon, discountAmount } })}
              className="w-full bg-[#0A5C44] hover:bg-[#064E3B] text-white font-bold text-xs py-4 rounded-full shadow-lg shadow-[#0A5C44]/25 hover:shadow-xl hover:shadow-[#0A5C44]/35 transition uppercase tracking-wider flex items-center justify-center gap-2 cursor-pointer active:scale-[0.99]"
            >
              <span>Proceed To Checkout</span>
              <ArrowRight className="w-4 h-4" />
            </button>

            <div className="flex items-center justify-center text-[11px] text-stone-400 space-x-1.5 pt-2">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
              <span>256-Bit SSL Encrypted & Razorpay Verified</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
