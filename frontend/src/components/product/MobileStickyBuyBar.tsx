import React, { useState } from 'react';
import { ShoppingBag, Heart, Zap, Check } from 'lucide-react';
import { Product } from '@/types';
import { useAuthStore } from '@/store/authStore';
import { useBadgeStore } from '@/store/badgeStore';
import { useNavigate } from 'react-router-dom';
import api from '@/services/api';

interface MobileStickyBuyBarProps {
  product: Product;
  quantity?: number;
  isWishlisted?: boolean;
}

export const MobileStickyBuyBar: React.FC<MobileStickyBuyBarProps> = ({
  product,
  quantity = 1,
  isWishlisted = false,
}) => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  const { fetchCounts } = useBadgeStore();

  const [wishlisted, setWishlisted] = useState(isWishlisted);
  const [adding, setAdding] = useState(false);
  const [added, setAdded] = useState(false);

  const handleWishlistToggle = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    setWishlisted(!wishlisted);
    try {
      await api.post(`/wishlist/${product.id}`);
      fetchCounts();
    } catch {
      setWishlisted(wishlisted);
    }
  };

  const handleAddToCart = async (directCheckout: boolean = false) => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    setAdding(true);
    try {
      await api.post('/cart/items', { productId: product.id, quantity });
      fetchCounts();
      if (directCheckout) {
        navigate('/checkout');
      } else {
        setAdded(true);
        setTimeout(() => setAdded(false), 2000);
      }
    } catch (err) {
      console.error('Failed to add item to bag:', err);
    } finally {
      setAdding(false);
    }
  };

  return (
    <div
      className="lg:hidden fixed bottom-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-md border-t border-stone-200 px-4 py-2.5 shadow-[0_-6px_25px_rgba(0,0,0,0.08)] flex items-center justify-between gap-3 animate-fadeIn"
      style={{
        paddingBottom: 'max(0.75rem, env(safe-area-inset-bottom, 0.75rem))',
      }}
    >
      {/* Price Summary */}
      <div className="flex flex-col shrink-0">
        <div className="flex items-baseline gap-1.5">
          <span className="text-base font-extrabold text-stone-900 font-sans tabular-nums">
            ₹{product.sellingPrice.toLocaleString('en-IN')}
          </span>
          {product.mrp > product.sellingPrice && (
            <span className="text-[11px] text-stone-400 line-through tabular-nums">
              ₹{product.mrp.toLocaleString('en-IN')}
            </span>
          )}
        </div>
        <span className="text-[9.5px] text-[#0A4D40] font-bold uppercase tracking-wider">
          {product.discountPercentage > 0
            ? `${product.discountPercentage}% OFF • Free Delivery`
            : 'Authentic Pure Silk'}
        </span>
      </div>

      {/* Action Buttons */}
      <div className="flex items-center gap-2 flex-1 justify-end">
        {/* Wishlist Button */}
        <button
          onClick={handleWishlistToggle}
          aria-label="Wishlist"
          className="w-10 h-10 rounded-full border border-stone-200 bg-stone-50 flex items-center justify-center text-stone-700 hover:text-[#0A4D40] active:scale-95 transition-all"
        >
          <Heart
            className={`w-4 h-4 ${
              wishlisted ? 'fill-[#0A4D40] text-[#0A4D40]' : 'text-stone-700'
            }`}
          />
        </button>

        {/* Add to Bag Button */}
        <button
          onClick={() => handleAddToCart(false)}
          disabled={adding || product.stock <= 0}
          className="flex-1 py-2.5 px-3 bg-stone-900 hover:bg-stone-800 text-white rounded-full text-xs font-bold uppercase tracking-wider flex items-center justify-center gap-1.5 transition-all active:scale-98 shadow-sm disabled:opacity-50"
        >
          {added ? (
            <>
              <Check className="w-3.5 h-3.5 text-emerald-400" />
              <span>Added!</span>
            </>
          ) : (
            <>
              <ShoppingBag className="w-3.5 h-3.5" />
              <span>{adding ? 'Adding...' : 'Add to Bag'}</span>
            </>
          )}
        </button>

        {/* Buy Now Instant Checkout Button */}
        <button
          onClick={() => handleAddToCart(true)}
          disabled={adding || product.stock <= 0}
          className="flex-1 py-2.5 px-3 bg-[#0A4D40] hover:bg-[#062E28] text-[#D4AF37] rounded-full text-xs font-bold uppercase tracking-wider flex items-center justify-center gap-1 transition-all active:scale-98 shadow-md shadow-[#0A4D40]/30 disabled:opacity-50"
        >
          <Zap className="w-3.5 h-3.5 fill-[#D4AF37]" />
          <span>Buy Now</span>
        </button>
      </div>
    </div>
  );
};
