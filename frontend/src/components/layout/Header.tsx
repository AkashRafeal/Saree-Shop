import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { 
  ShoppingBag, 
  Heart, 
  Search, 
  User, 
  Home,
  Menu, 
  X, 
  LogOut, 
  ShieldCheck, 
  ShoppingCart,
  PhoneCall
} from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import api from '@/services/api';

export const Header: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();

  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [userDropdownOpen, setUserDropdownOpen] = useState(false);
  const [cartCount, setCartCount] = useState(0);
  const [wishlistCount, setWishlistCount] = useState(0);

  // Fetch cart & wishlist count
  useEffect(() => {
    if (isAuthenticated) {
      api.get('/cart')
        .then((res) => setCartCount(res.data?.data?.totalItems || 0))
        .catch(() => {});
      api.get('/wishlist')
        .then((res) => setWishlistCount(res.data?.data?.totalItems || 0))
        .catch(() => {});
    } else {
      setCartCount(0);
      setWishlistCount(0);
    }
  }, [isAuthenticated]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/shop?search=${encodeURIComponent(searchQuery.trim())}`);
      setSearchQuery('');
    }
  };

  const isAdmin = user?.role === 'ROLE_ADMIN';

  const navLinks = [
    { name: 'HOME', href: '/' },
    { name: 'ALL SAREES', href: '/shop' },
    { name: 'NEW ARRIVALS', href: '/shop?category=new-arrivals' },
    { name: 'SILK SAREES', href: '/shop?category=kanchipuram-silk' },
    { name: 'BANARASI', href: '/shop?category=banarasi-silk' },
    { name: 'WEDDING COLLECTION', href: '/shop?category=bridal-sarees' },
    { name: 'COTTON & LINEN', href: '/shop?category=cotton-linen' },
    { name: 'SALE / OFFERS', href: '/shop?sale=true' },
    { name: 'REVIEWS', href: '/reviews' },
  ];

  const isLinkActive = (href: string) => {
    const pathname = location.pathname;
    const search = location.search;
    const params = new URLSearchParams(search);
    const cat = params.get('category');
    const occ = params.get('occasion');
    const isSale = params.get('sale') === 'true';

    // 1. Home
    if (href === '/') {
      return pathname === '/' && (!search || search === '');
    }

    // 2. Reviews Page
    if (href === '/reviews') {
      return pathname === '/reviews';
    }

    // 3. New Arrivals
    if (href.includes('category=new-arrivals')) {
      return pathname === '/shop' && cat === 'new-arrivals';
    }

    // 4. Specific Categories
    if (href.includes('category=kanchipuram-silk')) {
      return pathname === '/shop' && (cat === 'kanchipuram-silk' || cat === 'silk-sarees');
    }
    if (href.includes('category=banarasi-silk')) {
      return pathname === '/shop' && (cat === 'banarasi-silk' || cat === 'banarasi');
    }
    if (href.includes('category=bridal-sarees')) {
      return pathname === '/shop' && (cat === 'bridal-sarees' || cat === 'wedding' || occ === 'Bridal' || occ === 'Wedding');
    }
    if (href.includes('category=cotton-linen')) {
      return pathname === '/shop' && (cat === 'cotton-linen' || cat === 'cotton');
    }

    // 5. Sale / Offers
    if (href.includes('sale=true')) {
      return pathname === '/shop' && isSale;
    }

    // 6. All Sarees / Shop catalog
    if (href === '/shop') {
      return pathname === '/shop' && !cat && !isSale;
    }

    return (pathname + search) === href;
  };

  return (
    <>
      {/* Top Announcement Bar in Soft Rose Blush */}
      <div className="bg-[#FFF1F2] border-b border-rose-100 text-[#D81B60] text-xs font-medium py-1.5 px-4 text-center tracking-wide flex items-center justify-center space-x-2">
        <PhoneCall className="w-3 h-3 text-[#D81B60] shrink-0" />
        <span className="truncate">
          For styling assistance & custom bridal orders, WhatsApp us at <strong>+91 98765 43210</strong> • Insured Pan-India Express Delivery
        </span>
      </div>

      {/* Main Luxury Header */}
      <header className="sticky top-0 z-40 bg-white/98 backdrop-blur-md border-b border-stone-200 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-20 gap-4">
            {/* Mobile Menu Trigger */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="lg:hidden p-2 text-stone-700 hover:text-[#D81B60]"
              aria-label="Toggle menu"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>

            {/* Brand Logo */}
            <Link
              to="/"
              onClick={() => {
                setMobileMenuOpen(false);
                window.scrollTo({ top: 0, behavior: 'smooth' });
              }}
              aria-label="SareeAura Home"
              className="flex flex-col shrink-0 cursor-pointer select-none no-underline hover:no-underline border-none bg-transparent outline-none focus:outline-none focus-visible:outline-none transition-opacity duration-200 hover:opacity-90 group"
            >
              <span className="font-serif text-2xl sm:text-3xl tracking-widest font-extrabold text-stone-900 uppercase leading-tight select-none">
                Saree<span className="text-[#D81B60]">Aura</span>
              </span>
              <span className="text-[9px] tracking-[0.3em] uppercase text-stone-400 font-sans -mt-1 font-semibold select-none">
                The Festive & Bridal Edit
              </span>
            </Link>

            {/* Center Pill Search Bar (Matching Reference Image) */}
            <div className="hidden sm:flex flex-1 max-w-lg mx-4">
              <form onSubmit={handleSearchSubmit} className="w-full relative flex items-center">
                <Search className="w-4 h-4 text-stone-400 absolute left-4 pointer-events-none" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search for embroidery, Banarasi, Kanchipuram and more..."
                  className="w-full pl-10 pr-20 py-2.5 bg-stone-100 hover:bg-stone-100/80 focus:bg-white border border-transparent focus:border-[#D81B60] rounded-full text-xs text-stone-800 placeholder-stone-400 focus:outline-none transition shadow-inner"
                />
                <button
                  type="submit"
                  className="absolute right-1.5 bg-[#D81B60] hover:bg-[#C2185B] text-white text-[11px] font-semibold px-3 py-1.5 rounded-full transition shadow-sm"
                >
                  Search
                </button>
              </form>
            </div>

            {/* Right Action Icons: Wishlist, User Account, Shopping Bag */}
            <div className="flex items-center space-x-5 sm:space-x-6 shrink-0">
              {/* Wishlist */}
              <Link
                to="/wishlist"
                aria-label="Wishlist"
                className="text-stone-700 hover:text-[#D81B60] transition-colors relative"
              >
                <Heart className="w-5 h-5" />
                {wishlistCount > 0 && (
                  <span className="absolute -top-1.5 -right-2 bg-[#D81B60] text-white text-[10px] w-4 h-4 rounded-full flex items-center justify-center font-bold">
                    {wishlistCount}
                  </span>
                )}
              </Link>

              {/* Shopping Bag */}
              <Link
                to="/cart"
                aria-label="Cart"
                className="text-stone-700 hover:text-[#D81B60] transition-colors relative"
              >
                <ShoppingBag className="w-5 h-5" />
                {cartCount > 0 && (
                  <span className="absolute -top-1.5 -right-2 bg-[#D81B60] text-white text-[10px] w-4 h-4 rounded-full flex items-center justify-center font-bold">
                    {cartCount}
                  </span>
                )}
              </Link>

              {/* User Account */}
              <div className="relative">
                {isAuthenticated ? (
                  <div>
                    <button
                      onClick={() => setUserDropdownOpen(!userDropdownOpen)}
                      className="flex items-center space-x-1.5 text-stone-700 hover:text-[#D81B60] focus:outline-none"
                    >
                      <div className="w-8 h-8 rounded-full bg-rose-50 border border-rose-200 text-[#D81B60] font-serif text-sm font-bold flex items-center justify-center shadow-sm">
                        {user?.firstName?.charAt(0) || 'U'}
                      </div>
                    </button>

                    {userDropdownOpen && (
                      <div className="absolute right-0 mt-3 w-56 bg-white rounded-xl shadow-xl border border-stone-200 py-2 z-50 divide-y divide-stone-100">
                        <div className="px-4 py-2.5">
                          <p className="text-xs font-semibold text-stone-900 truncate">
                            {user?.firstName} {user?.lastName}
                          </p>
                          <p className="text-[11px] text-stone-500 truncate">{user?.email}</p>
                        </div>

                        <div className="py-1 text-xs">
                          <Link
                            to="/my-orders"
                            onClick={() => setUserDropdownOpen(false)}
                            className="flex items-center px-4 py-2 text-stone-700 hover:bg-stone-50 hover:text-[#D81B60]"
                          >
                            <ShoppingCart className="w-4 h-4 mr-2.5 text-stone-400" />
                            My Orders & Tracking
                          </Link>
                          {isAdmin && (
                            <Link
                              to="/admin"
                              onClick={() => setUserDropdownOpen(false)}
                              className="flex items-center px-4 py-2 text-[#D81B60] font-semibold hover:bg-rose-50"
                            >
                              <ShieldCheck className="w-4 h-4 mr-2.5 text-[#D81B60]" />
                              Admin Portal
                            </Link>
                          )}
                        </div>

                        <div className="py-1">
                          <button
                            onClick={() => {
                              logout();
                              setUserDropdownOpen(false);
                              navigate('/login');
                            }}
                            className="w-full flex items-center px-4 py-2 text-xs text-red-600 hover:bg-red-50 text-left"
                          >
                            <LogOut className="w-4 h-4 mr-2.5" />
                            Logout
                          </button>
                        </div>
                      </div>
                    )}
                  </div>
                ) : (
                  <Link
                    to="/login"
                    className="text-stone-700 hover:text-[#D81B60] transition-colors"
                    aria-label="Login"
                  >
                    <User className="w-5 h-5" />
                  </Link>
                )}
              </div>
            </div>
          </div>

          {/* Sub Navigation Bar with Exact Category Tabs */}
          <nav className="hidden lg:flex items-center justify-center space-x-6 xl:space-x-7 py-2.5 border-t border-stone-100 text-[11px] font-semibold tracking-wider">
            {navLinks.map((item) => {
              const active = isLinkActive(item.href);
              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`relative transition-all uppercase py-1.5 outline-none focus:outline-none focus-visible:outline-none focus:ring-0 select-none ${
                    active
                      ? 'text-[#D81B60] font-bold'
                      : 'text-stone-700 font-semibold hover:text-[#D81B60]'
                  }`}
                >
                  <span>{item.name}</span>
                  {active && (
                    <span className="absolute -bottom-1 left-0 right-0 h-[2.5px] bg-[#D81B60] rounded-full shadow-sm shadow-[#D81B60]/30" />
                  )}
                </Link>
              );
            })}
          </nav>
        </div>

        {/* Mobile Navigation Drawer */}
        {mobileMenuOpen && (
          <div className="lg:hidden bg-white border-t border-stone-200 px-4 pt-4 pb-6 space-y-3 shadow-lg">
            <form onSubmit={handleSearchSubmit} className="relative flex items-center mb-4">
              <Search className="w-4 h-4 text-stone-400 absolute left-3" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search sarees..."
                className="w-full pl-9 pr-4 py-2 bg-stone-100 border border-transparent rounded-lg text-xs"
              />
            </form>

            {navLinks.map((link) => {
              const active = isLinkActive(link.href);
              return (
                <Link
                  key={link.name}
                  to={link.href}
                  onClick={() => {
                    setMobileMenuOpen(false);
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                  }}
                  className={`flex items-center justify-between py-2 px-3 rounded-lg text-xs font-semibold tracking-wider transition outline-none focus:outline-none focus-visible:outline-none focus:ring-0 select-none ${
                    active
                      ? 'bg-rose-50 text-[#D81B60] font-bold border-l-4 border-[#D81B60]'
                      : 'text-stone-800 hover:bg-stone-50 hover:text-[#D81B60]'
                  }`}
                >
                  <div className="flex items-center gap-2">
                    {link.name === 'HOME' && <Home className={`w-4 h-4 ${active ? 'text-[#D81B60]' : 'text-stone-400'}`} />}
                    <span>{link.name}</span>
                  </div>
                  {active && <span className="w-1.5 h-1.5 rounded-full bg-[#D81B60]" />}
                </Link>
              );
            })}

            {isAdmin && (
              <Link
                to="/admin"
                onClick={() => setMobileMenuOpen(false)}
                className="block py-2 text-xs font-bold text-[#D81B60]"
              >
                Admin Portal
              </Link>
            )}
          </div>
        )}
      </header>
    </>
  );
};
