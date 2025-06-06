document.addEventListener('DOMContentLoaded', function() {
    // Initialize pagination functionality
    initPagination();

    // Initialize price filter functionality
    initPriceFilter();

    // Initialize type filter functionality
    initTypeFilter();
});

function initPagination() {
    // Get the pagination container
    const pagination = document.getElementById('pagination');

    if (pagination) {
        // Add click event listeners to all pagination links
        pagination.addEventListener('click', function(e) {
            // Check if the clicked element is a pagination link
            if (e.target.classList.contains('page-link') && !e.target.parentElement.classList.contains('disabled')) {
                e.preventDefault();

                // Get the page number from the data-page attribute
                const page = e.target.getAttribute('data-page');

                if (page) {
                    // Load the products for the selected page
                    loadProductsPage(page);
                }
            }
        });
    }
}

function initPriceFilter() {
    // Get the price filter form
    const priceFilterForm = document.getElementById('price-filter-form');

    if (priceFilterForm) {
        // Add change event listeners to all checkboxes
        const checkboxes = priceFilterForm.querySelectorAll('input[type="checkbox"]');

        checkboxes.forEach(function(checkbox) {
            checkbox.addEventListener('change', function() {
                // If "All Prices" is checked, uncheck all other options
                if (this.id === 'price-all' && this.checked) {
                    checkboxes.forEach(function(cb) {
                        if (cb.id !== 'price-all') {
                            cb.checked = false;
                        }
                    });
                } else if (this.checked) {
                    // If any other option is checked, uncheck "All Prices"
                    document.getElementById('price-all').checked = false;
                }

                // Load the first page with the selected price filters
                loadProductsPage(1);
            });
        });
    }
}

function initTypeFilter() {
    // Get the type filter form
    const typeFilterForm = document.getElementById('type-filter-form');

    if (typeFilterForm) {
        // Add change event listeners to all checkboxes
        const checkboxes = typeFilterForm.querySelectorAll('input[type="checkbox"]');

        checkboxes.forEach(function(checkbox) {
            checkbox.addEventListener('change', function() {
                // If "All Types" is checked, uncheck all other options
                if (this.id === 'type-all' && this.checked) {
                    checkboxes.forEach(function(cb) {
                        if (cb.id !== 'type-all') {
                            cb.checked = false;
                        }
                    });
                } else if (this.checked) {
                    // If any other option is checked, uncheck "All Types"
                    document.getElementById('type-all').checked = false;
                }

                // Load the first page with the selected type filters
                loadProductsPage(1);
            });
        });
    }
}

function loadProductsPage(page) {
    // Show loading indicator
    const productContainer = document.getElementById('product-container');
    productContainer.innerHTML = '<div class="col-12 text-center"><p>Loading...</p></div>';

    // Build the query parameters
    let params = new URLSearchParams();
    params.append('page', page);

    // Add search query if exists
    const queryParam = new URLSearchParams(window.location.search).get('query');
    if (queryParam) {
        params.append('query', queryParam);
    }

    // Add price range filters
    const priceFilterForm = document.getElementById('price-filter-form');
    if (priceFilterForm) {
        const checkedBoxes = priceFilterForm.querySelectorAll('input[type="checkbox"]:checked');
        checkedBoxes.forEach(function(checkbox) {
            params.append('priceRange', checkbox.value);
        });
    }

    // Add type filters
    const typeFilterForm = document.getElementById('type-filter-form');
    if (typeFilterForm) {
        const checkedBoxes = typeFilterForm.querySelectorAll('input[type="checkbox"]:checked');
        checkedBoxes.forEach(function(checkbox) {
            params.append('typeId', checkbox.value);
        });
    }

    // Make AJAX request
    fetch('shop?' + params.toString(), {
        method: 'GET',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => response.text())
    .then(html => {
        // Update the product container with the new products
        productContainer.innerHTML = html;

        // Update the URL without refreshing the page
        const newUrl = window.location.pathname + '?' + params.toString();
        history.pushState({}, '', newUrl);

        // Reinitialize pagination to attach event listeners to the new pagination elements
        initPagination();

        // Reinitialize price filter to attach event listeners to the checkboxes
        initPriceFilter();

        // Reinitialize type filter to attach event listeners to the checkboxes
        initTypeFilter();

        // Update pagination UI
        updatePaginationUI(page);
    })
    .catch(error => {
        console.error('Error loading products:', error);
        productContainer.innerHTML = '<div class="col-12 text-center"><p>Error loading products. Please try again.</p></div>';
    });
}

function updatePaginationUI(currentPage) {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;

    // Update active page
    const pageLinks = pagination.querySelectorAll('.page-link');
    pageLinks.forEach(link => {
        const page = link.getAttribute('data-page');
        const listItem = link.parentElement;

        if (page === currentPage) {
            listItem.classList.add('active');
        } else {
            listItem.classList.remove('active');
        }
    });

    // Update previous/next buttons
    try {
        // Find the Previous button by its text content
        const prevLink = Array.from(pagination.querySelectorAll('.page-link')).find(link => 
            link.textContent.trim() === 'Previous'
        );

        // Find the Next button by its text content
        const nextLink = Array.from(pagination.querySelectorAll('.page-link')).find(link => 
            link.textContent.trim() === 'Next'
        );

        if (prevLink && nextLink) {
            const prevButton = prevLink.closest('.page-item');
            const nextButton = nextLink.closest('.page-item');

            // Handle Previous button state
            if (currentPage === '1') {
                prevButton.classList.add('disabled');
            } else {
                prevButton.classList.remove('disabled');
            }

            // Get total pages by finding the highest page number
            const pageNumbers = Array.from(pagination.querySelectorAll('.page-link[data-page]'))
                .map(link => parseInt(link.getAttribute('data-page')))
                .filter(num => !isNaN(num));
            const totalPages = pageNumbers.length > 0 ? Math.max(...pageNumbers) : 1;

            // Handle Next button state
            if (parseInt(currentPage) >= totalPages) {
                nextButton.classList.add('disabled');
            } else {
                nextButton.classList.remove('disabled');
            }
        }
    } catch (error) {
        console.error('Error updating pagination UI:', error);
    }
}
