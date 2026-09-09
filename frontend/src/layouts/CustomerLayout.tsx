import React from 'react';
import { Outlet, Link } from 'react-router-dom';
import { ShoppingBag, Heart, Search, User, Menu, Sparkles } from 'lucide-react';

export const CustomerLayout: React.FC = () => {
  return (
    <div className="min-h-screen flex flex-col bg-brand-silk text-brand-charcoal">
      {/* Luxury Announcement Bar */}
      <div className="bg-brand-maroon text-brand-gold-light text-xs tracking-widest uppercase text-center py-2 px-4 flex items-center justify-center space-x-2 font-medium">
        <Sparkles className="w-3.5 h-3.5 text-brand-gold" />
        <span>Complimentary Express Shipping on Orders Above ₹5,000 • Authentic Handloom Guarantee</span>
        <Sparkles className="w-3.5 h-3.5 text-brand-gold" />
      </div>

      {/* Main Luxury Header */}
      <header className="sticky top-0 z-50 bg-white/90 backdrop-blur-md border-b border-brand-cream/80 transition-all">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-20">
            {/* Mobile Menu Trigger */}
            <button className="md:hidden p-2 text-brand-charcoal hover:text-brand-maroon focus:outline-none">
              <Menu className="w-6 h-6" />
            </button>

            {/* Brand Logo */}
            <div className="flex-shrink-0 flex items-center">
              <Link to="/" className="flex flex-col items-center">
                <span className="font-serif text-2xl sm:text-3xl tracking-widest font-bold text-brand-maroon uppercase">
                  Saree<span className="text-brand-gold">Aura</span>
                </span>
                <span className="text-[9px] tracking-[0.3em] uppercase text-stone-500 font-sans -mt-1">
                  Heritage • Elegance
                </span>
              </Link>
            </div>

            {/* Desktop Navigation Links */}
            <nav className="hidden md:flex space-x-8 text-sm font-medium tracking-wide">
              <Link to="/" className="text-brand-maroon font-semibold hover:text-brand-gold transition-colors">
                Home
              </Link>
              <Link to="/shop" className="text-stone-700 hover:text-brand-maroon transition-colors">
                All Sarees
              </Link>
              <Link to="/shop?category=kanchipuram" className="text-stone-700 hover:text-brand-maroon transition-colors">
                Kanchipuram
              </Link>
              <Link to="/shop?category=banarasi" className="text-stone-700 hover:text-brand-maroon transition-colors">
                Banarasi
              </Link>
              <Link to="/shop?category=bridal" className="text-stone-700 hover:text-brand-maroon transition-colors">
                Bridal Edit
              </Link>
              <Link to="/about" className="text-stone-700 hover:text-brand-maroon transition-colors">
                Our Story
              </Link>
            </nav>

            {/* Actions: Search, Wishlist, Cart, Profile */}
            <div className="flex items-center space-x-5">
              <button aria-label="Search" className="text-stone-600 hover:text-brand-maroon transition-colors">
                <Search className="w-5 h-5" />
              </button>
              <Link to="/wishlist" aria-label="Wishlist" className="text-stone-600 hover:text-brand-maroon transition-colors relative">
                <Heart className="w-5 h-5" />
                <span className="absolute -top-1.5 -right-2 bg-brand-maroon text-white text-[10px] w-4 h-4 rounded-full flex items-center justify-center font-bold">
                  0
                </span>
              </Link>
              <Link to="/cart" aria-label="Cart" className="text-stone-600 hover:text-brand-maroon transition-colors relative">
                <ShoppingBag className="w-5 h-5" />
                <span className="absolute -top-1.5 -right-2 bg-brand-gold text-brand-charcoal text-[10px] w-4 h-4 rounded-full flex items-center justify-center font-bold">
                  0
                </span>
              </Link>
              <Link to="/login" aria-label="Account" className="text-stone-600 hover:text-brand-maroon transition-colors">
                <User className="w-5 h-5" />
              </Link>
            </div>
          </div>
        </div>
      </header>

      {/* Main Page Content */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Luxury Footer */}
      <footer className="bg-stone-900 text-stone-300 border-t border-brand-gold/30 pt-12 pb-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
            <div className="space-y-3">
              <span className="font-serif text-2xl font-bold text-brand-gold-light uppercase tracking-wider">
                Saree<span className="text-brand-gold">Aura</span>
              </span>
              <p className="text-xs text-stone-400 leading-relaxed">
                Curating India's finest handloom weaves. From royal Kanchipuram silks to regal Banarasi brocades, we preserve royal heritage with timeless artistry.
              </p>
            </div>
            <div>
              <h4 className="font-serif text-sm font-semibold text-brand-gold uppercase tracking-wider mb-3">
                Collections
              </h4>
              <ul className="space-y-2 text-xs text-stone-400">
                <li><Link to="/shop?category=kanchipuram" className="hover:text-brand-gold transition">Pure Kanchipuram Silk</Link></li>
                <li><Link to="/shop?category=banarasi" className="hover:text-brand-gold transition">Banarasi Handloom Weaves</Link></li>
                <li><Link to="/shop?category=tussar" className="hover:text-brand-gold transition">Tussar & Raw Silk</Link></li>
                <li><Link to="/shop?category=chanderi" className="hover:text-brand-gold transition">Chanderi & Organza</Link></li>
              </ul>
            </div>
            <div>
              <h4 className="font-serif text-sm font-semibold text-brand-gold uppercase tracking-wider mb-3">
                Customer Care
              </h4>
              <ul className="space-y-2 text-xs text-stone-400">
                <li><Link to="/faq" className="hover:text-brand-gold transition">Shipping & Delivery</Link></li>
                <li><Link to="/faq" className="hover:text-brand-gold transition">Authenticity Guarantee</Link></li>
                <li><Link to="/faq" className="hover:text-brand-gold transition">Returns & Exchange</Link></li>
                <li><Link to="/contact" className="hover:text-brand-gold transition">Contact Concierge</Link></li>
              </ul>
            </div>
            <div>
              <h4 className="font-serif text-sm font-semibold text-brand-gold uppercase tracking-wider mb-3">
                System Status
              </h4>
              <div className="p-3 rounded bg-stone-800 border border-stone-700 text-xs">
                <div className="flex items-center space-x-2 text-emerald-400 font-medium mb-1">
                  <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
                  <span>Phase 1 Architecture Ready</span>
                </div>
                <p className="text-[11px] text-stone-400">Modular Monolith Backend • React 18 SPA • MySQL 8 Persistence</p>
              </div>
            </div>
          </div>
          <div className="border-t border-stone-800 pt-6 text-center text-xs text-stone-500">
            © {new Date().getFullYear()} SareeAura Luxury Silks. All Rights Reserved. Crafted with Passion & Pride.
          </div>
        </div>
      </footer>
    </div>
  );
};
