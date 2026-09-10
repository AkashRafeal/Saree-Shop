import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { CheckCircle2, ShieldCheck, MapPin, CreditCard, Sparkles, Plus, Check } from 'lucide-react';
import { Address, Cart, Order } from '@/types';
import { useAuthStore } from '@/store/authStore';
import api from '@/services/api';

export const CheckoutPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { isAuthenticated } = useAuthStore();

  const [addresses, setAddresses] = useState<Address[]>([]);
  const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null);
  const [showNewAddressModal, setShowNewAddressModal] = useState(false);
  const [cart, setCart] = useState<Cart | null>(null);
  const [paymentMethod, setPaymentMethod] = useState('RAZORPAY');
  const [placingOrder, setPlacingOrder] = useState(false);
  const [confirmedOrder, setConfirmedOrder] = useState<Order | null>(null);

  // New Address fields
  const [newFullName, setNewFullName] = useState('');
  const [newPhone, setNewPhone] = useState('');
  const [newAddressLine1, setNewAddressLine1] = useState('');
  const [newCity, setNewCity] = useState('');
  const [newState, setNewState] = useState('');
  const [newPostalCode, setNewPostalCode] = useState('');

  const couponCode = (location.state as any)?.appliedCoupon || '';
  const couponDiscount = (location.state as any)?.discountAmount || 0;

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    // Load addresses and cart
    api.get('/addresses')
      .then((res) => {
        const addrList = res.data?.data || [];
        setAddresses(addrList);
        if (addrList.length > 0) {
          const def = addrList.find((a: Address) => a.isDefault) || addrList[0];
          setSelectedAddressId(def.id);
        }
      })
      .catch((err) => console.error(err));

    api.get('/cart')
      .then((res) => {
        const c = res.data?.data;
        if (!c || c.items.length === 0) {
          navigate('/cart');
        } else {
          setCart(c);
        }
      })
      .catch((err) => console.error(err));
  }, [isAuthenticated]);

  const handleAddNewAddress = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await api.post('/addresses', {
        fullName: newFullName,
        phone: newPhone,
        addressLine1: newAddressLine1,
        city: newCity,
        state: newState,
        postalCode: newPostalCode,
        country: 'India',
        addressType: 'HOME',
        isDefault: true,
      });
      const created = res.data?.data;
      setAddresses([created, ...addresses]);
      setSelectedAddressId(created.id);
      setShowNewAddressModal(false);
    } catch (err) {
      console.error(err);
    }
  };

  const handlePlaceOrder = async () => {
    if (!selectedAddressId) {
      alert('Please select or add a delivery address');
      return;
    }

    setPlacingOrder(true);
    try {
      const res = await api.post('/orders', {
        addressId: selectedAddressId,
        couponCode,
        paymentMethod,
      });
      setConfirmedOrder(res.data?.data);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to place order');
    } finally {
      setPlacingOrder(false);
    }
  };

  if (confirmedOrder) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-16 text-center space-y-6">
        <div className="w-20 h-20 mx-auto rounded-full bg-emerald-100 flex items-center justify-center text-emerald-600">
          <CheckCircle2 className="w-12 h-12" />
        </div>
        <div className="space-y-2">
          <span className="text-xs font-bold uppercase tracking-widest text-emerald-700">
            Payment & Order Confirmed
          </span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-stone-900">
            Thank You for Your Patronage!
          </h1>
          <p className="text-xs text-stone-500 max-w-md mx-auto">
            Your royal saree order <span className="font-bold text-stone-800">{confirmedOrder.orderNumber}</span> has been confirmed. Our master artisans are now preparing your handloom for insured dispatch.
          </p>
        </div>

        <div className="bg-white p-6 rounded-2xl border border-stone-200 text-left text-xs space-y-3 shadow-sm max-w-md mx-auto">
          <div className="flex justify-between">
            <span className="text-stone-500">Order Reference</span>
            <span className="font-bold text-stone-800">{confirmedOrder.orderNumber}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-stone-500">Insured Amount Paid</span>
            <span className="font-bold text-stone-800">₹{confirmedOrder.totalAmount.toLocaleString('en-IN')}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-stone-500">Courier Partner</span>
            <span className="font-bold text-stone-800">{confirmedOrder.courierName}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-stone-500">Tracking Code</span>
            <span className="font-bold text-brand-maroon">{confirmedOrder.trackingNumber}</span>
          </div>
        </div>

        <div className="flex justify-center gap-4 pt-4">
          <Link
            to={`/my-orders/${confirmedOrder.id}`}
            className="bg-brand-maroon hover:bg-brand-maroon-dark text-white font-bold text-xs py-3.5 px-6 rounded-xl uppercase tracking-wider transition"
          >
            Track Order Live
          </Link>
          <Link
            to="/shop"
            className="bg-stone-100 hover:bg-stone-200 text-stone-800 font-bold text-xs py-3.5 px-6 rounded-xl uppercase tracking-wider transition"
          >
            Continue Shopping
          </Link>
        </div>
      </div>
    );
  }

  if (!cart) return null;

  const grandTotal = Math.max(0, cart.subtotal - couponDiscount + cart.shippingCharge);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      <div>
        <span className="text-xs font-bold uppercase tracking-widest text-brand-maroon">
          Secure Checkout
        </span>
        <h1 className="font-serif text-3xl font-bold text-stone-900 mt-1">
          Finalize Your Handloom Order
        </h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
        {/* Left: Steps Accordion */}
        <div className="lg:col-span-2 space-y-6">
          {/* Step 1: Delivery Address */}
          <div className="bg-white p-6 rounded-2xl border border-stone-200/80 shadow-sm space-y-4">
            <div className="flex items-center justify-between border-b border-stone-100 pb-3">
              <h3 className="font-serif text-lg font-bold text-stone-900 flex items-center gap-2">
                <MapPin className="w-5 h-5 text-brand-maroon" />
                <span>1. Select Delivery Address</span>
              </h3>
              <button
                onClick={() => setShowNewAddressModal(true)}
                className="text-xs font-semibold text-brand-maroon hover:underline flex items-center gap-1"
              >
                <Plus className="w-3.5 h-3.5" />
                <span>Add New Address</span>
              </button>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {addresses.map((addr) => (
                <div
                  key={addr.id}
                  onClick={() => setSelectedAddressId(addr.id)}
                  className={`p-4 rounded-xl border-2 cursor-pointer transition text-xs space-y-1 relative ${
                    selectedAddressId === addr.id
                      ? 'border-brand-maroon bg-brand-maroon/5 shadow-sm'
                      : 'border-stone-200 hover:border-stone-300'
                  }`}
                >
                  {selectedAddressId === addr.id && (
                    <span className="absolute top-3 right-3 w-5 h-5 rounded-full bg-brand-maroon text-white flex items-center justify-center">
                      <Check className="w-3 h-3" />
                    </span>
                  )}
                  <p className="font-bold text-stone-900">{addr.fullName}</p>
                  <p className="text-stone-600">{addr.addressLine1}</p>
                  <p className="text-stone-600">{addr.city}, {addr.state} - {addr.postalCode}</p>
                  <p className="text-stone-500 pt-1">Phone: {addr.phone}</p>
                </div>
              ))}
            </div>

            {/* Modal for adding address */}
            {showNewAddressModal && (
              <form onSubmit={handleAddNewAddress} className="p-4 bg-stone-50 rounded-xl border border-stone-200 space-y-3 mt-4">
                <h4 className="font-serif text-sm font-bold">Add Shipping Address</h4>
                <div className="grid grid-cols-2 gap-3 text-xs">
                  <input
                    type="text"
                    placeholder="Full Name"
                    required
                    value={newFullName}
                    onChange={(e) => setNewFullName(e.target.value)}
                    className="p-2 rounded border border-stone-300 col-span-2 sm:col-span-1"
                  />
                  <input
                    type="text"
                    placeholder="Phone"
                    required
                    value={newPhone}
                    onChange={(e) => setNewPhone(e.target.value)}
                    className="p-2 rounded border border-stone-300 col-span-2 sm:col-span-1"
                  />
                  <input
                    type="text"
                    placeholder="Address Line 1"
                    required
                    value={newAddressLine1}
                    onChange={(e) => setNewAddressLine1(e.target.value)}
                    className="p-2 rounded border border-stone-300 col-span-2"
                  />
                  <input
                    type="text"
                    placeholder="City"
                    required
                    value={newCity}
                    onChange={(e) => setNewCity(e.target.value)}
                    className="p-2 rounded border border-stone-300"
                  />
                  <input
                    type="text"
                    placeholder="State"
                    required
                    value={newState}
                    onChange={(e) => setNewState(e.target.value)}
                    className="p-2 rounded border border-stone-300"
                  />
                  <input
                    type="text"
                    placeholder="Postal Code"
                    required
                    value={newPostalCode}
                    onChange={(e) => setNewPostalCode(e.target.value)}
                    className="p-2 rounded border border-stone-300 col-span-2"
                  />
                </div>
                <div className="flex justify-end gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setShowNewAddressModal(false)}
                    className="px-3 py-1.5 text-xs text-stone-600"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="bg-brand-maroon text-white font-bold text-xs px-4 py-1.5 rounded"
                  >
                    Save Address
                  </button>
                </div>
              </form>
            )}
          </div>

          {/* Step 2: Payment Method */}
          <div className="bg-white p-6 rounded-2xl border border-stone-200/80 shadow-sm space-y-4">
            <h3 className="font-serif text-lg font-bold text-stone-900 flex items-center gap-2 border-b border-stone-100 pb-3">
              <CreditCard className="w-5 h-5 text-brand-maroon" />
              <span>2. Payment Gateway</span>
            </h3>

            <div className="space-y-3">
              <label
                onClick={() => setPaymentMethod('RAZORPAY')}
                className={`flex items-center justify-between p-4 rounded-xl border-2 cursor-pointer transition ${
                  paymentMethod === 'RAZORPAY' ? 'border-brand-maroon bg-brand-maroon/5' : 'border-stone-200'
                }`}
              >
                <div className="flex items-center space-x-3">
                  <div className="w-4 h-4 rounded-full border-2 border-brand-maroon flex items-center justify-center">
                    {paymentMethod === 'RAZORPAY' && <span className="w-2 h-2 rounded-full bg-brand-maroon" />}
                  </div>
                  <div>
                    <span className="font-bold text-xs text-stone-900 block">Razorpay Test Mode Gateway</span>
                    <span className="text-[11px] text-stone-500">UPI, NetBanking, Credit/Debit Cards, Wallets</span>
                  </div>
                </div>
                <span className="text-[10px] font-bold uppercase tracking-wider text-emerald-700 bg-emerald-100 px-2 py-0.5 rounded">
                  Instant Verification
                </span>
              </label>
            </div>
          </div>
        </div>

        {/* Right: Order Summary Sidebar */}
        <div className="space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-stone-200/80 shadow-sm space-y-5">
            <h3 className="font-serif text-lg font-bold text-stone-900 border-b border-stone-100 pb-3">
              Order Review
            </h3>

            {/* Items list preview */}
            <div className="divide-y divide-stone-100 max-h-56 overflow-y-auto pr-1 space-y-2">
              {cart.items.map((item) => (
                <div key={item.id} className="pt-2 flex items-center justify-between text-xs">
                  <div className="flex items-center space-x-2">
                    <img src={item.imageUrl} alt="" className="w-10 h-12 object-cover rounded bg-stone-100" />
                    <div className="max-w-[130px]">
                      <p className="font-semibold text-stone-900 truncate">{item.productName}</p>
                      <p className="text-[10px] text-stone-400">Qty: {item.quantity}</p>
                    </div>
                  </div>
                  <span className="font-bold text-stone-900">₹{item.subtotal.toLocaleString('en-IN')}</span>
                </div>
              ))}
            </div>

            {/* Price Calculations */}
            <div className="space-y-2.5 text-xs border-t border-stone-100 pt-4">
              <div className="flex justify-between text-stone-600">
                <span>Subtotal</span>
                <span>₹{cart.subtotal.toLocaleString('en-IN')}</span>
              </div>

              {couponDiscount > 0 && (
                <div className="flex justify-between text-emerald-700 font-semibold">
                  <span>Coupon Discount ({couponCode})</span>
                  <span>-₹{couponDiscount.toLocaleString('en-IN')}</span>
                </div>
              )}

              <div className="flex justify-between text-stone-600">
                <span>Insured Express Shipping</span>
                <span>{cart.shippingCharge === 0 ? <span className="text-emerald-700 font-semibold">FREE</span> : `₹${cart.shippingCharge}`}</span>
              </div>

              <div className="flex justify-between text-base font-bold text-stone-900 border-t border-stone-200 pt-3">
                <span>Total Payable</span>
                <span>₹{grandTotal.toLocaleString('en-IN')}</span>
              </div>
            </div>

            <button
              onClick={handlePlaceOrder}
              disabled={placingOrder || !selectedAddressId}
              className="w-full bg-brand-maroon hover:bg-brand-maroon-dark text-white font-bold text-xs py-4 rounded-xl shadow-lg transition uppercase tracking-wider flex items-center justify-center gap-2"
            >
              <Sparkles className="w-4 h-4" />
              <span>{placingOrder ? 'Confirming Order...' : `Pay ₹${grandTotal.toLocaleString('en-IN')} & Confirm`}</span>
            </button>

            <div className="flex items-center justify-center text-[11px] text-stone-400 space-x-1.5 pt-1">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
              <span>Razorpay Verified Sandbox Environment</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
