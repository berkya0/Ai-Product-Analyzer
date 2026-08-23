import React from 'react';

export default function CompareProductCard({ product, onSelect, isSelected }) {
  return (
    <div className="flex items-center gap-4 p-3 bg-[#F9FCFF] border-blue-100 rounded-xl shadow-sm hover:shadow-md transition-all">
      {/* Ürün Görseli */}
      <img
        src={product.imageUrl}
        alt={product.name}
        className="w-20 h-20 object-cover rounded-lg border border-gray-100 flex-shrink-0"
      />

      {/* Ürün Bilgisi ve Buton */}
      <div className="flex flex-col justify-between h-full flex-grow gap-2">
        <h3 className="text-sm font-semibold text-gray-800 line-clamp-2">
          {product.name}
        </h3>

        <div>
          <button
            onClick={() => onSelect(product)}
            disabled={isSelected}
            className={`px-4 py-1.5 text-xs font-medium rounded-lg transition-colors ${
              isSelected
                ? 'bg-gray-200 text-gray-500 cursor-not-allowed'
                : 'bg-[#0F385A] hover:bg-[#0A263D] text-white'
            }`}
          >
            {isSelected ? 'Seçildi' : 'Karşılaştır'}
          </button>
        </div>
      </div>
    </div>
  );
}